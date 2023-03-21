package net.villenium.authservice.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SecurityException
import net.villenium.authservice.AUTHORIZATION_HEADER
import net.villenium.authservice.AUTHORIZATION_PREFIX
import net.villenium.authservice.config.error.ApiError
import net.villenium.authservice.config.error.AuthenticationEntryPointImpl.Companion.sendResponse
import net.villenium.authservice.pojo.User
import net.villenium.authservice.service.TokenService
import net.villenium.authservice.service.UserService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthorizationFilter(
    private val userService: UserService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        try {
            val token: String? = request.getHeader(AUTHORIZATION_HEADER)
            if (token != null && token.startsWith(AUTHORIZATION_PREFIX)) {
                val claims: Claims = tokenService
                    .parseToken(token.removePrefix(AUTHORIZATION_PREFIX))
                authorize(claims)
                filterChain.doFilter(request, response)
                return
            }
        } catch (ex: JwtException) {
            when (ex) {
                is MalformedJwtException, is SecurityException -> {
                    sendResponse(ApiError.TOKEN_INVALID.build(), response)
                }
                is ExpiredJwtException -> {
                    sendResponse(ApiError.TOKEN_EXPIRED.build(), response)
                }
            }
            return
        }
        SecurityContextHolder.clearContext()
        filterChain.doFilter(request, response)
    }

    private fun authorize(claims: Claims) {
        val user: User = userService.find(claims.subject)
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(user, null, null)
    }
}
