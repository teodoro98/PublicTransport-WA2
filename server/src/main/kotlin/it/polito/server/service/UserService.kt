package it.polito.server.service

import it.polito.server.dto.UserDTO
import it.polito.server.dto.UserProvDTO
import it.polito.server.dto.UserSlimDTO
import it.polito.server.dto.ValidationDTO

interface UserService {

    fun validateUserData(user: UserDTO)

    fun validateUserEmail(validation : ValidationDTO): UserSlimDTO

    fun registerUser(user: UserDTO) : Pair<UserProvDTO, Long>

    fun pruneExpiredActivation()

}