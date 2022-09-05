package it.polito.traveler.dto

import java.time.LocalDate

data class UserDetailsLiteDTO(
    val id: Long?,
    val username:String?,
    val address: String,
    val dateOfBirth: LocalDate,
    val telephoneNumber: String
)
