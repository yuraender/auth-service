package net.villenium.authservice.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class BeanConfig {

    @Bean
    fun gson(): Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create()
}
