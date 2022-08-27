package it.polito.login.service

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.*

@Service
class EmailServiceImpl: EmailService {

    @Autowired
    private lateinit var mailSender:JavaMailSender

    override fun sendEmail(to : String, activationCode : Long, provitionalId : UUID) {

        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, false, "utf-8")

            val content = "<H1>Active your account!</H1> <div> Activation code:$activationCode</div><div>ProvitionalID: $provitionalId"

            helper.setFrom("pushz.andrea@rob.corbo");
            helper.setTo(to);
            helper.setSubject("Activate your account");
            mimeMessage.setContent(content, "text/html");
            mailSender.send(mimeMessage);
        } catch (e: MessagingException){

    }

    }
}