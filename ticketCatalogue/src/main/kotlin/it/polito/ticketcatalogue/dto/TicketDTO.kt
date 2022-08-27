package it.polito.ticketcatalogue.dto

data class TicketDTO(
    val ticketID: Long?,
    val price: Double,
    val type: String,
)