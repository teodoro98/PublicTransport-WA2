package it.polito.ticketcatalogue.dto

data class UserOrder(
    val id: Long,
    val username : String,
    val password: String,
    val authorities: String
)
