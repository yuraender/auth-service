package net.villenium.authservice.config.error

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.villenium.authservice.AuthService
import net.villenium.authservice.pojo.User
import org.springframework.beans.factory.getBean
import org.springframework.expression.EvaluationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.io.PrintWriter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class AuthenticationEntryPointImpl : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        sendResponse(ApiError.AUTH_ACCESS_DENIED.build(), response)
    }

    @ExceptionHandler(AccessDeniedException::class, EvaluationException::class)
    fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        exception: RuntimeException?
    ) {
        val auth: Authentication? = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            val format = "%s was trying to access %s%n"
            if (auth is AnonymousAuthenticationToken) {
                System.out.printf(format, auth.name, request.requestURI)
            } else if (auth is UsernamePasswordAuthenticationToken) {
                System.out.printf(format, (auth.principal as User).login, request.requestURI)
            }
        }
        sendResponse(ApiError.AUTH_ACCESS_DENIED.build(), response)
    }

    companion object {
        @JvmStatic
        fun sendResponse(responseEntity: ResponseEntity<Any>, response: HttpServletResponse) {
            val gson: Gson = AuthService.instance!!.getBean(Gson::class)
            val body: JsonObject = gson.toJsonTree(responseEntity).asJsonObject.getAsJsonObject("body")
            response.status = body["code"].asInt
            if (response.status == HttpStatus.UNAUTHORIZED.value()) {
                response.addHeader("WWW-Authenticate", "Basic realm=\"Realm\"")
            }
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            val writer: PrintWriter = response.writer
            writer.print(body)
            writer.flush()
        }
    }
}
