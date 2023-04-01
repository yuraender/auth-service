package net.villenium.authservice

import org.springframework.http.HttpStatus

abstract class ApiException(message: String, val status: HttpStatus) : RuntimeException(message)

class UserNotFoundException : ApiException("User is not found", HttpStatus.NOT_FOUND)

class UserAlreadyExistException : ApiException("User already exists", HttpStatus.BAD_REQUEST)

class IncorrectTokenException : ApiException("Token is invalid", HttpStatus.UNAUTHORIZED)

class IncorrectPasswordException : ApiException("Incorrect login or password", HttpStatus.FORBIDDEN)

class InvalidCodeException : ApiException("Activation code is invalid", HttpStatus.BAD_REQUEST)

class ValidationException(message: String) : RuntimeException(message)
