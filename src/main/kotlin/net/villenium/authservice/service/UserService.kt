package net.villenium.authservice.service

import com.github.benmanes.caffeine.cache.Cache
import net.villenium.authservice.EMAIL_ADDRESS_PATTERN
import net.villenium.authservice.IncorrectPasswordException
import net.villenium.authservice.InvalidCodeException
import net.villenium.authservice.PASSWORD_PATTERN
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
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val tokenService: TokenService,

    private val activationCache: Cache<User, Int>,
    private val sessionCache: Cache<String, String>
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
        if (userRepository.findByLogin(user.login) != null
            || userRepository.findByEmail(user.email) != null) {
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

    fun activate(email: String, code: Int) {
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
        save(entry.key, true)
    }

    fun login(login: String, password: String, ip: String): String {
        val user: User = find(login)
        if (!validateAccount(login, password)) {
            throw IncorrectPasswordException()
        }
        sessionCache.put(login, ip)
        return tokenService.createToken(user)
    }

    fun changePassword(login: String, password: String): String {
        val user: User = find(login)
        user.password = password
        return tokenService.createToken(save(user, false))
    }

    fun isRegistered(loginOrEmail: String): Boolean {
        return userRepository.findByLogin(loginOrEmail) != null
                || userRepository.findByEmail(loginOrEmail) != null
    }

    fun validateAccount(login: String, password: String): Boolean {
        val user: User = find(login)
        return passwordEncoder.matches(password, user.password)
    }

    fun hasSession(login: String, ip: String): Boolean {
        return sessionCache.asMap().any { it.key == login && it.value == ip }
    }

    private fun save(user: User, create: Boolean): User {
        validate(user)
        if (!PASSWORD_PATTERN.matcher(user.password).find()) {
            throw ValidationException("Password is invalid")
        }
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
        if (user.login.isEmpty()) {
            throw ValidationException("Login is empty")
        }
        if (user.password.isEmpty()) {
            throw ValidationException("Password is empty")
        }
        if (!EMAIL_ADDRESS_PATTERN.matcher(user.email).find()) {
            throw ValidationException("Email is empty or invalid")
        }
        return user
    }
}
