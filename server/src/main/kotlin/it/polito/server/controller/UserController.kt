package it.polito.server.controller

import it.polito.server.dto.UserDTO
import it.polito.server.service.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
class UserController {

    @Autowired
    private lateinit var us: UserServiceImpl

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    fun validate(@RequestBody user: UserDTO) {
        us.validateUser(user)
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    fun registration(@RequestBody user: UserDTO) {
        us.registerUser(user)
    }

}