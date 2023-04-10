package net.villenium.authservice

import java.util.regex.Pattern

const val AUTHORIZATION_SECRET: String = "VWxkS2FHSkZNV2hrUlhSb1kyMWtkbUp0VW1nPQ=="
const val AUTHORIZATION_HEADER: String = "Authorization"
const val AUTHORIZATION_PREFIX: String = "Bearer "
val PASSWORD_PATTERN: Pattern =
    Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\w\\W]{6,24}$")
val EMAIL_ADDRESS_PATTERN: Pattern =
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

val EMAIL_MESSAGE: String = """
    Дорогой игрок Villenium Network,

    Мы рады приветствовать вас на нашем сервере и благодарим за регистрацию. Мы надеемся, что вы проведете здесь время с удовольствием и найдете новых друзей.

    Для завершения процесса регистрации, мы отправили вам код подтверждения. Пожалуйста, используйте его, чтобы активировать свой аккаунт и начать играть на проекте Villenium Network.

    Код подтверждения: %d

    Большое спасибо, что выбрали наш сервер и доверили нам свое время. Если у вас возникнут какие-либо вопросы или затруднения, наша команда администрации всегда готова помочь.

    Удачной игры на Villenium Network!
    С уважением,
    Администрация Villenium Network.
""".trimIndent()
