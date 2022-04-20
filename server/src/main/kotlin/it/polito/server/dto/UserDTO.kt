package it.polito.server.dto

import it.polito.server.entity.User

data class UserDTO(
    val id:Long?,
    val name:String,
    val email:String
    )

fun User.toDTO(): UserDTO{
    return UserDTO(id, name, email)
}