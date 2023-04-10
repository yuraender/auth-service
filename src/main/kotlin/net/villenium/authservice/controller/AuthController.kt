package net.villenium.authservice.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import net.villenium.authservice.IncorrectTokenException
import net.villenium.authservice.pojo.User
import net.villenium.authservice.service.UserService
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth", produces = [MediaType.APPLICATION_JSON_VALUE])
@Api(description = "Operations pertaining to authorization")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/register")
    @ApiOperation("Register a user", response = Boolean::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Status"),
        ApiResponse(code = 400, message = "User already exists"),
        ApiResponse(code = 500, message = "Validation error")
    ])
    fun create(
        @ApiParam("The user body", required = true)
        @RequestBody user: User
    ): Boolean {
        return userService.register(user)
    }

    @PostMapping("/activate")
    @ApiOperation("Activate an account")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Account has been activated"),
        ApiResponse(code = 400, message = "Activation code is invalid"),
        ApiResponse(code = 404, message = "User is not found"),
        ApiResponse(code = 500, message = "Validation error")
    ])
    fun activate(
        @ApiParam("The email an account linked with", required = true)
        @RequestParam email: String,
        @ApiParam("An activation code", required = true)
        @RequestParam code: Int
    ) {
        userService.activate(email, code)
    }

    @PostMapping("/login")
    @ApiOperation("Login into an account", response = String::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Token",
            examples = Example(ExampleProperty(token, mediaType = "application/json"))),
        ApiResponse(code = 403, message = "Incorrect login or password"),
        ApiResponse(code = 404, message = "User is not found"),
        ApiResponse(code = 500, message = "Validation error")
    ])
    fun login(
        @ApiParam("The login an account linked with", required = true)
        @RequestParam login: String,
        @ApiParam("Password of an account", required = true)
        @RequestParam password: String,
        @ApiParam("Session IP", required = true)
        @RequestParam ip: String
    ): String {
        return userService.login(login, password, ip)
    }

    @PostMapping("/changePassword")
    @ApiOperation("Change password of an account", response = String::class)
    @ApiImplicitParams(value = [
        ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, paramType = "header")
    ])
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Token",
            examples = Example(ExampleProperty(token, mediaType = "application/json"))),
        ApiResponse(code = 401, message = "Token is invalid"),
        ApiResponse(code = 404, message = "User is not found"),
        ApiResponse(code = 500, message = "Validation error")
    ])
    fun changePassword(
        @ApiParam("The login an account linked with", required = true)
        @RequestParam login: String,
        @ApiParam("New password of an account", required = true)
        @RequestParam password: String,
        @AuthenticationPrincipal adminUser: User?
    ): String {
        if (adminUser != null && adminUser.login != login) {
            throw IncorrectTokenException()
        }
        return userService.changePassword(login, password)
    }

    @GetMapping("/isRegistered")
    @ApiOperation("Check if login or email is used", response = Boolean::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Status")
    ])
    fun isRegistered(
        @ApiParam("Login or email an account linked with", required = true)
        @RequestParam loginOrEmail: String
    ): Boolean {
        return userService.isRegistered(loginOrEmail)
    }

    @GetMapping("/validate")
    @ApiOperation("Validate an account", response = Boolean::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Status"),
        ApiResponse(code = 404, message = "User is not found"),
        ApiResponse(code = 500, message = "Validation error")
    ])
    fun validate(
        @ApiParam("The login an account linked with", required = true)
        @RequestParam login: String,
        @ApiParam("Password of an account", required = true)
        @RequestParam password: String
    ): Boolean {
        return userService.validateAccount(login, password)
    }

    @GetMapping("/hasSession")
    @ApiOperation("Validate a session", response = Boolean::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Status"),
        ApiResponse(code = 404, message = "User is not found"),
        ApiResponse(code = 500, message = "Validation error")
    ])
    fun hasSession(
        @ApiParam("The login an account linked with", required = true)
        @RequestParam login: String,
        @ApiParam("Session IP", required = true)
        @RequestParam ip: String
    ): Boolean {
        return userService.hasSession(login, ip)
    }

    companion object {
        private const val token: String = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJZdXJhRW5kZXIiLCJwYXNzd29yZCI6IiQyYSQxMCQ0anZqTXRkTHJkdHAybFVmMEVsS2h1L0V4L2ZXRnlDSlRn" +
                "T2RZUkhVcmdtcS9Pc2FiUXR1eSIsImVtYWlsIjoieXVyYWVuZGVyQHlhbmRleC5ydSIsImlhdCI6MTY4MDM3MDkxMiwiZXhwI" +
                "joxNjgwNDU3MzEyfQ.1FrWRz0AIK5Z_OD3HMxn8EWSXzVIDTcVOt3aihuygfo"
    }
}
