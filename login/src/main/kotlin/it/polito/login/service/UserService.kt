package it.polito.login.service

import it.polito.login.dto.*
import it.polito.login.entity.User

interface UserService {

    fun validateUserData(user: UserDTO, roles: MutableList<User.Role>)

    fun validateUserEmail(validation : ValidationDTO): UserSlimDTO

    fun registerUser(user: UserDTO, roles: MutableList<User.Role>) : Pair<UserProvDTO?, Long?>

    fun pruneExpiredActivation()

    fun loginUser(user: UserLoginDTO) : JwtDTO

}