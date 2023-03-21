package net.villenium.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import net.villenium.authservice.AUTHORIZATION_SECRET
import net.villenium.authservice.pojo.User
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class TokenService {

    fun createToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.login)
            .claim("password", user.password)
            .claim("email", user.email)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
            .signWith(Keys.hmacShaKeyFor(AUTHORIZATION_SECRET.toByteArray()))
            .compact()
    }

    fun parseToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(AUTHORIZATION_SECRET.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
    }
}
