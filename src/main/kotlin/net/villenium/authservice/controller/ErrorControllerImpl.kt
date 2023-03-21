package net.villenium.authservice.controller

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(value = ["/error"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ErrorControllerImpl(
    private val errorAttributes: ErrorAttributes,
    private val gson: Gson
) : ErrorController {

    @RequestMapping
    fun error(webRequest: WebRequest?, httpRequest: HttpServletRequest): ResponseEntity<Any> {
        val attributes: MutableMap<String, Any> =
            errorAttributes.getErrorAttributes(webRequest, includeStackTrace(httpRequest))
        val body = JsonObject()
        body.addProperty("message", attributes["message"] as String)
        body.addProperty("code", attributes["status"] as Int)
        val trace: String? = attributes["trace"] as String?
        if (trace != null) {
            val lines: Array<String> = trace
                .replace("\t", "").split("\r\n".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            body.add("trace", gson.toJsonTree(lines))
        }
        return ResponseEntity.status(attributes["status"] as Int).body(body)
    }

    override fun getErrorPath(): String {
        return "/error"
    }

    private fun includeStackTrace(request: HttpServletRequest): Boolean {
        val trace: String = request.getParameter("trace")
            ?: return false
        return !"false".equals(trace, true)
    }
}
