package it.polito.traveler.dto

data class UserOrder(
    val id: Long,
    val username : String,
    val password: String,
    val authorities: String
)
