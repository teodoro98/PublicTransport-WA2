package it.polito.ticketcatalogue.dto

data class BuyTickets(
    val cmd: String,
    val quantity: Int,
    val zones: String
)
