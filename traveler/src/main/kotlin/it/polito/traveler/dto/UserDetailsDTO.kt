package it.polito.traveler.dto

import it.polito.traveler.entity.TicketPurchased
import java.time.LocalDate

data class UserDetailsDTO (
    val id: Long?,
    val username:String,
    val address: String,
    val dateOfBirth:LocalDate,
    val telephoneNumber: Long
)