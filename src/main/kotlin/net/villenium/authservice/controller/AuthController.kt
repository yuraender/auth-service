package net.villenium.authservice.controller

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
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(userService.register(user))
    }

    @PostMapping("/activate")
    fun activate(
        @RequestParam email: String,
        @RequestParam code: Int
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(userService.activate(email, code))
    }

    @PostMapping("/login")
    fun login(
        @RequestParam login: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        return ResponseEntity.ok().body(userService.login(login, password))
    }

    @PostMapping("/changePassword")
    fun changePassword(
        @RequestBody user: User,
        @AuthenticationPrincipal adminUser: User?
    ): ResponseEntity<Any> {
        if (adminUser != null && adminUser.id != user.id) {
            return ApiError.AUTH_ACCESS_DENIED.build()
        }
        return ResponseEntity.ok().body(userService.changePassword(user))
    }

    @GetMapping("/validate")
    fun validate(
        @RequestParam login: String,
        @RequestParam password: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok().body(userService.validateAccount(login, password))
    }
}
