package it.polito.server.service

import it.polito.server.dto.UserDTO

interface UserService {

    fun validateUser(user: UserDTO)

    fun registerUser(user: UserDTO)

}