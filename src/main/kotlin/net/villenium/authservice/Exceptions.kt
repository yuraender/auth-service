package net.villenium.authservice

abstract class ApiException(message: String) : RuntimeException(message)

class UserNotFoundException : ApiException("User is not found")

class ValidationException(message: String) : RuntimeException(message)
