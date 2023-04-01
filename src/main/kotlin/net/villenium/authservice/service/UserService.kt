package net.villenium.authservice.service

import com.github.benmanes.caffeine.cache.Cache
import net.villenium.authservice.EMAIL_ADDRESS_PATTERN
import net.villenium.authservice.IncorrectPasswordException
import net.villenium.authservice.InvalidCodeException
import net.villenium.authservice.UserAlreadyExistException
import net.villenium.authservice.UserNotFoundException
import net.villenium.authservice.ValidationException
import net.villenium.authservice.pojo.User
import net.villenium.authservice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val activationCache: Cache<User, Int>,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val tokenService: TokenService
) {

    fun find(login: String): User {
        val user: User = userRepository.findByLogin(login)
            ?: throw UserNotFoundException()
        validate(user)
        return user
    }

    fun register(user: User): Boolean {
        validate(user)
        activationCache.asMap()
            .keys
            .stream()
            .anyMatch { it.email == user.email }
            .let { if (it) throw UserAlreadyExistException() }
        if (userRepository.findByLogin(user.login!!) != null
            || userRepository.findByEmail(user.email!!) != null) {
            throw UserAlreadyExistException()
        }

        val code = (100000..999999).random()
        activationCache.put(user, code)
        emailService.sendMessage(
            user.email,
            "Подтверждение регистрации",
            "Код для активации: $code"
        )
        return true
    }

    fun activate(email: String, code: Int): String {
        val entry: Map.Entry<User, Int> = activationCache.asMap()
            .entries
            .stream()
            .filter { it.key.email == email }
            .findAny()
            .orElseThrow { UserNotFoundException() }
        if (entry.value != code) {
            throw InvalidCodeException()
        }
        activationCache.invalidate(entry.key)
        val createdUser: User = save(entry.key, true)
        return tokenService.createToken(createdUser)
    }

    fun login(login: String, password: String): String {
        val user: User = find(login)
        if (!validateAccount(login, password)) {
            throw IncorrectPasswordException()
        }
        return tokenService.createToken(user)
    }

    fun changePassword(user: User): String {
        val updatedUser: User = save(user, false)
        return tokenService.createToken(updatedUser)
    }

    fun validateAccount(login: String, password: String): Boolean {
        val user: User = find(login)
        return passwordEncoder.matches(password, user.password)
    }

    private fun save(user: User, create: Boolean): User {
        validate(user)
        return if (create) {
            user.password = passwordEncoder.encode(user.password)
            userRepository.saveAndFlush(user)
        } else {
            val dbUser: User = userRepository.findById(user.id)
                .orElseThrow { UserNotFoundException() }
            dbUser.password = passwordEncoder.encode(user.password)
            userRepository.saveAndFlush(dbUser)
        }
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
