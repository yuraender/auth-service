package net.villenium.authservice.config

import com.google.gson.Gson
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebMvc
class WebMvcConfig(
    private val gson: Gson
) : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        val gsonHttpMessageConverter = GsonHttpMessageConverter()
        gsonHttpMessageConverter.gson = gson
        converters.add(gsonHttpMessageConverter)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/auth/**")
            .allowedOrigins("https://villenium.net")
            .allowedMethods("GET", "POST")
            .allowedHeaders("*")
            .maxAge(3600)
    }
}
