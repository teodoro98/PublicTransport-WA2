package it.polito.login.controller

import it.polito.login.dto.*
import it.polito.login.entity.User
import it.polito.login.security.UserDetailsImpl
import it.polito.login.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/turnstile")
class TurnstyleController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    fun registration(@RequestBody user: TurnstileDTO) {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val u = TurnstileDTO.toUserDTO(user, (auth.principal as UserDetailsImpl).getEmail())
        val roles = mutableListOf(User.Role.ROLE_TURNSTILE)
        u.active = true
        userService.validateUserData(u, roles)
        userService.registerUser(u, roles)
    }
}