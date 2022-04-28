package it.polito.server.service

import org.springframework.stereotype.Service
import java.util.*

@Service
interface EmailService {

    fun sendEmail(to : String, activationCode : Long, provitionalId : UUID)
}