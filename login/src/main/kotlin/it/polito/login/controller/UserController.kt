package it.polito.login.controller

import it.polito.login.dto.*
import it.polito.login.entity.User
import it.polito.login.service.EmailServiceImpl
import it.polito.login.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime



@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private lateinit var us: UserService

    @Autowired
    private lateinit var emailService: EmailServiceImpl

    private var logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun validate(@RequestBody validation : ValidationDTO): UserSlimDTO {
        return us.validateUserEmail(validation)
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registration(@RequestBody user: UserDTO): UserProvDTO {
        val roles = mutableListOf(User.Role.ROLE_COSTUMER)
        us.validateUserData(user, roles)
        user.active = false
        val tuple = us.registerUser(user, roles)
        if(tuple.first != null && tuple.second != null) {
            tuple.first!!.provisional_id?.let { emailService.sendEmail(user.email, tuple.second!!, it) }
            logger.info("Email sent at ${LocalDateTime.now()}")
            return tuple.first!!
        }

        return UserProvDTO(null, user.email)
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun login(@RequestBody user: UserLoginDTO): JwtDTO {
        val jwt = us.loginUser(user)
        return jwt
    }

}