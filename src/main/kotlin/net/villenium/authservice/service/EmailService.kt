package net.villenium.authservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage

@Service
class EmailService(
    private val mailSender: JavaMailSender,

    @Value("\${spring.mail.sender-name}")
    private val senderName: String,

    @Value("\${spring.mail.sender-address}")
    private val senderAddress: String
) {

    fun sendMessage(to: String, subject: String, text: String) {
        sendMessage(to, subject, text, MimeTypeUtils.TEXT_PLAIN)
    }

    fun sendMessage(to: String, subject: String, text: String, mimeType: MimeType) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setFrom(senderAddress, senderName)
        helper.setTo(to)
        helper.setSubject(subject)
        MimeBodyPart().let {
            it.setText(text, "UTF-8")
            helper.mimeMultipart.addBodyPart(it)
        }
        mailSender.send(message)
    }
}
