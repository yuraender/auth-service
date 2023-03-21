package net.villenium.authservice.config.error

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.villenium.authservice.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

enum class ApiError(
    private val message: String,
    private val httpStatus: HttpStatus
) {
    METHOD_NOT_FOUND("Method not found", HttpStatus.NOT_FOUND),
    MISSING_PARAMETER("Required request parameter '%s' is missing", HttpStatus.BAD_REQUEST),
    MISSING_BODY("Required request body is missing", HttpStatus.BAD_REQUEST),
    EXCEPTION("%s", HttpStatus.INTERNAL_SERVER_ERROR);

    fun build(vararg params: Any?): ResponseEntity<Any> {
        return ResponseEntity.status(httpStatus).body(getBody(httpStatus, *params))
    }

    fun build(httpStatus: HttpStatus, vararg params: Any?): ResponseEntity<Any> {
        return ResponseEntity.status(httpStatus).body(getBody(httpStatus, *params))
    }

    private fun getBody(httpStatus: HttpStatus, vararg params: Any?): JsonObject {
        val body = JsonObject()
        body.addProperty("message", message.format(*params))
        body.addProperty("code", httpStatus.value())
        if (this == EXCEPTION && params.size > 1) {
            val trace: Array<String> = (params[1] as String)
                .replace("\t", "").split("\r\n".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            body.add("trace", AuthService.instance!!.getBean(Gson::class.java).toJsonTree(trace))
        }
        return body
    }
}
