package it.polito.ticketcatalogue.dto

import java.time.LocalDate

data class UserDetailsDTO (
    val id: Long?,
    val username:String,
    val address: String,
    val dateOfBirth:LocalDate,
    val telephoneNumber: String
)