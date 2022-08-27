package it.polito.login.service

import it.polito.login.dto.*

interface UserService {

    fun validateUserData(user: UserDTO)

    fun validateUserEmail(validation : ValidationDTO): UserSlimDTO

    fun registerUser(user: UserDTO) : Pair<UserProvDTO, Long>

    fun pruneExpiredActivation()

    fun loginUser(user: UserLoginDTO) : JwtDTO

}