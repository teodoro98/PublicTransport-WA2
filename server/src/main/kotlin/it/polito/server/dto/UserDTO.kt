package it.polito.server.dto

import it.polito.server.entity.Role
import it.polito.server.entity.User

data class UserDTO(
    val id:Long?,
    val nickname:String,
    val email:String,
    val password: String,
    val active: Boolean,
    val role: Role
    )

