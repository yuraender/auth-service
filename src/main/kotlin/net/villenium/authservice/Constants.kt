package net.villenium.authservice

import java.util.regex.Pattern

const val AUTHORIZATION_SECRET: String = "VWxkS2FHSkZNV2hrUlhSb1kyMWtkbUp0VW1nPQ=="
const val AUTHORIZATION_HEADER: String = "Authorization"
const val AUTHORIZATION_PREFIX: String = "Bearer "
val PASSWORD_PATTERN: Pattern =
    Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\w\\W]{6,24}$")
val EMAIL_ADDRESS_PATTERN: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
