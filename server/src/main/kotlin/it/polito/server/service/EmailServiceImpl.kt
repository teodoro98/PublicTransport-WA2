package it.polito.server.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl


class EmailServiceImpl: EmailService {

    private val javaMailSender = JavaMailSenderImpl()

    override fun sendEmail(email: String) {

        val msg = SimpleMailMessage()
        msg.setTo("feafea@gmail.com", "andreacv1998@gmail.com")
        msg.setSubject("Testing from Spring Boot")
        msg.setText("Hello World \n Spring Boot Email")

        javaMailSender.send(msg)
    }
}