package it.polito.login.controller

import it.polito.login.dto.*
import it.polito.login.entity.User
import it.polito.login.service.EmailService
import it.polito.login.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/admin")
class AdminController {

    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var emailService: EmailService

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') and hasRole('RECRUITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun registration(@RequestBody user: AdminDTO): UserProvDTO {
        val u = AdminDTO.toUserDTO(user)
        val roles: MutableList<User.Role> = if(user.recruiter) {
            mutableListOf(User.Role.ROLE_ADMIN, User.Role.ROLE_RECRUITER)
        } else {
            mutableListOf(User.Role.ROLE_ADMIN)
        }
        userService.validateUserData(u, roles)
        u.active = true
        val tuple = userService.registerUser(u, roles)
        if(tuple.first != null && tuple.second != null) {
            tuple.first!!.provisional_id?.let { emailService.sendEmail(user.email, tuple.second!!, it) }
            println("Email sent at ${LocalDateTime.now()}")
            return tuple.first!!
        }

        return UserProvDTO(null, user.email)

    }
}