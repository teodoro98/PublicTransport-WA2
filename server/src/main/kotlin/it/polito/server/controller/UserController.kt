package it.polito.server.controller

import it.polito.server.dto.*
import it.polito.server.service.EmailServiceImpl
import it.polito.server.service.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt
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

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun login(@RequestBody user: UserLoginDTO): JwtDTO {
        val jwt = us.loginUser(user)
        return jwt
    }

}