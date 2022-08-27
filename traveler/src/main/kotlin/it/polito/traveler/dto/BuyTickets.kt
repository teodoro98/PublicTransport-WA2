package it.polito.traveler.dto

data class BuyTickets(
    val cmd: String,
    val quantity: Int,
    val zones: String
)
