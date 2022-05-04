package it.polito.server.service

import it.polito.server.dto.*

interface UserService {

    fun validateUserData(user: UserDTO)

    fun validateUserEmail(validation : ValidationDTO): UserSlimDTO

    fun registerUser(user: UserDTO) : Pair<UserProvDTO, Long>

    fun pruneExpiredActivation()

    fun loginUser(user: UserLoginDTO) : String

}