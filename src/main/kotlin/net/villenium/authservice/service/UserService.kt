package net.villenium.authservice.service

import com.github.benmanes.caffeine.cache.Cache
import net.villenium.authservice.EMAIL_ADDRESS_PATTERN
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
    private val tokenService: TokenService
) {

    fun find(login: String): User {
        val user: User = userRepository.findByLogin(login)
            ?: throw UserNotFoundException()
        validate(user)
        return user
    }

    fun register(user: User): String {
        validate(user)
        if (userRepository.findByLogin(user.login!!) != null
            || userRepository.findByEmail(user.email!!) != null) {
            throw UserAlreadyExistException()
        }
        val code = (100000..999999).random()
        activationCache.put(user, code)
        //TODO: отправка письма на почту игрока
        return "Письмо отправлено на ${user.email}. У вас 5 минут на активацию."
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
        val userDto: User = find(login)
        if (!validateAccount(login, password)) {
            throw ValidationException("Incorrect login or password")
        }
        return tokenService.createToken(userDto)
    }

    fun changePassword(user: User): String {
        val updatedUser: User = save(user, false)
        return tokenService.createToken(updatedUser)
    }

    fun validateAccount(login: String, password: String): Boolean {
        val userDto: User = find(login)
        return passwordEncoder.matches(password, userDto.password)
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