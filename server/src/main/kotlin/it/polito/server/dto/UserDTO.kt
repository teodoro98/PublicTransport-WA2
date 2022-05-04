package it.polito.server.dto

data class UserDTO(
    val id:Long?,
    val nickname:String,
    val email:String,
    val password: String,
    val active: Boolean
    )

