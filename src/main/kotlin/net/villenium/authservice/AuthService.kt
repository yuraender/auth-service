package net.villenium.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class AuthService {

    companion object {
        var instance: ApplicationContext? = null

        @JvmStatic
        fun main(args: Array<String>) {
            val nArgs: MutableList<String> = args.toMutableList()
            listOf(
                "classpath:application.properties",
                "classpath:mail.properties",
                "classpath:sql.properties"
            ).let {
                nArgs += "--spring.config.location=${it.joinToString(",")}"
            }
            instance = runApplication<AuthService>(*nArgs.toTypedArray())
        }
    }
}
