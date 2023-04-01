package net.villenium.authservice.config

import io.swagger.models.Model
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .genericModelSubstitutes(ResponseEntity::class.java)
            .ignoredParameterTypes(Model::class.java)
            .select()
            .apis(RequestHandlerSelectors.basePackage("net.villenium.authservice.controller"))
            .paths(PathSelectors.ant("/api/auth/**"))
            .build()
            .useDefaultResponseMessages(false)
    }
}