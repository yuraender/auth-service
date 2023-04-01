package net.villenium.authservice.config.error

import net.villenium.authservice.ApiException
import net.villenium.authservice.ValidationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class ExceptionHandlerImpl : ResponseEntityExceptionHandler() {

    //Methods
    /* 405 -> 404 */ override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        return ApiError.METHOD_NOT_FOUND.build()
    }

    /* 400 */ override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        return ApiError.MISSING_PARAMETER.build(ex.parameterName)
    }

    /* 400 */ override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        return ApiError.MISSING_BODY.build()
    }

    /* 400 */ override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException, headers: HttpHeaders, status: HttpStatus, request: WebRequest
    ): ResponseEntity<Any> {
        return ApiError.MISSING_BODY.build()
    }

    //API Errors
    @ExceptionHandler(RuntimeException::class)
    protected /* 500 */ fun handleException(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<Any> {
        val status: HttpStatus = when (ex) {
            is ApiException -> ex.status
            is ValidationException -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        val trace: String? = request.getParameter("trace")
        return if (trace != null && !"false".equals(trace, true)) {
            ApiError.EXCEPTION.build(status, ex.message, getStackTraceAsString(ex))
        } else {
            ApiError.EXCEPTION.build(status, ex.message)
        }
    }

    private fun getStackTraceAsString(throwable: Throwable): String {
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }
}
