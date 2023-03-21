package net.villenium.authservice.config

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.villenium.authservice.pojo.User
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class BeanConfig {

    @Bean
    fun gson(): Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun activationCache(): Cache<User, Int> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(5))
        .build()
}
