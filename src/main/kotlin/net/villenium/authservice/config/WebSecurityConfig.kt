package net.villenium.authservice.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@EnableWebSecurity
class WebSecurityConfig(
    private val authorizationFilter: JwtAuthorizationFilter,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors().and()
            .csrf().disable()
            .addFilterAfter(authorizationFilter, BasicAuthenticationFilter::class.java)
            .authorizeRequests()
            .antMatchers("/auth/changePassword", "/auth/changePassword/**").authenticated()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
    }
}
