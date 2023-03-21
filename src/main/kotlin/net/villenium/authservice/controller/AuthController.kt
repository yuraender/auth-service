package net.villenium.authservice.controller

import com.google.gson.JsonObject
import net.villenium.authservice.config.error.ApiError
import net.villenium.authservice.pojo.User
import net.villenium.authservice.service.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth", produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    fun create(
        @RequestBody user: User
    ): ResponseEntity<Any> {
        val register: Boolean = userService.register(user)

        val response = JsonObject()
        response.addProperty("success", register)
        return ResponseEntity.ok().body(response)
    }

    @PostMapping("/activate")
    fun activate(
        @RequestParam email: String,
        @RequestParam code: Int
    ): ResponseEntity<Any> {
        val token: String = userService.activate(email, code)

        val response = JsonObject()
        response.addProperty("token", token)
        return ResponseEntity.ok().body(response)
    }

    @PostMapping("/login")
    fun login(
        @RequestParam login: String,
        @RequestParam password: String
    ): ResponseEntity<Any> {
        val token: String = userService.login(login, password)

        val response = JsonObject()
        response.addProperty("token", token)
        return ResponseEntity.ok().body(response)
    }

    @PostMapping("/changePassword")
    fun changePassword(
        @RequestBody user: User,
        @AuthenticationPrincipal adminUser: User?
    ): ResponseEntity<Any> {
        if (adminUser != null && adminUser.id != user.id) {
            return ApiError.AUTH_ACCESS_DENIED.build()
        }
        val token: String = userService.changePassword(user)

        val response = JsonObject()
        response.addProperty("token", token)
        return ResponseEntity.ok().body(response)
    }

    @GetMapping("/validate")
    fun validate(
        @RequestParam login: String,
        @RequestParam password: String
    ): ResponseEntity<Any> {
        val validateAccount: Boolean = userService.validateAccount(login, password)

        val response = JsonObject()
        response.addProperty("success", validateAccount)
        return ResponseEntity.ok().body(response)
    }
}
