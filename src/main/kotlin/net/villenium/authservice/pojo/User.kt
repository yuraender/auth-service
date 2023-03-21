package net.villenium.authservice.pojo

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(length = 16, nullable = false, unique = true)
    val login: String?,

    @Column(length = 100, nullable = false)
    var password: String?,

    @Column(length = 254, nullable = false, unique = true)
    val email: String?
)
