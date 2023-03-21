package net.villenium.authservice.service

import net.villenium.authservice.EMAIL_ADDRESS_PATTERN
import net.villenium.authservice.UserNotFoundException
import net.villenium.authservice.ValidationException
import net.villenium.authservice.pojo.User
import net.villenium.authservice.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun find(login: String): User {
        val user: User = userRepository.findByLogin(login)
            ?: throw UserNotFoundException()
        validate(user)
        return user
    }

    private fun validate(user: User): User {
        if (user.login.isNullOrEmpty()) {
            throw ValidationException("Login is null or empty")
        }
        if (user.password.isNullOrEmpty()) {
            throw ValidationException("Password is null or empty")
        }
        if (user.email.isNullOrEmpty()
            || !EMAIL_ADDRESS_PATTERN.matcher(user.email).find()) {
            throw ValidationException("Email is null or empty, or invalid")
        }
        return user
    }
}
