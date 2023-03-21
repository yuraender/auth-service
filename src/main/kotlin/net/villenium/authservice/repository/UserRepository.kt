package net.villenium.authservice.repository

import net.villenium.authservice.pojo.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {

    fun findByLogin(login: String): User?

    fun findByEmail(email: String): User?
}
