package net.villenium.authservice

abstract class ApiException(message: String) : RuntimeException(message)

class UserNotFoundException : ApiException("User is not found")

class UserAlreadyExistException : ApiException("User already exists")

class InvalidCodeException : ApiException("Activation code is invalid")

class ValidationException(message: String) : RuntimeException(message)
