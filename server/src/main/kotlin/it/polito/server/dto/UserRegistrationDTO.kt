package it.polito.server.dto

data class UserRegistrationDTO(
    val nickname: String,
    val password: String,
    val email: String
)
