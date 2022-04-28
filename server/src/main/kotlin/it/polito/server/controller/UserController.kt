package it.polito.server.controller

import it.polito.server.dto.UserDTO
import it.polito.server.dto.UserProvDTO
import it.polito.server.dto.UserSlimDTO
import it.polito.server.dto.ValidationDTO
import it.polito.server.service.EmailServiceImpl
import it.polito.server.service.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime


@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private lateinit var us: UserServiceImpl

    @Autowired
    private lateinit var email: EmailServiceImpl

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun validate(@RequestBody validation : ValidationDTO): UserSlimDTO {
        return us.validateUserEmail(validation)
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registration(@RequestBody user: UserDTO): UserProvDTO {
        us.validateUserData(user)
        val tupla = us.registerUser(user)
        tupla.first.provisional_id?.let { email.sendEmail(user.email, tupla.second, it) }
        println("Email sent at ${LocalDateTime.now()}")
        return tupla.first
    }

}