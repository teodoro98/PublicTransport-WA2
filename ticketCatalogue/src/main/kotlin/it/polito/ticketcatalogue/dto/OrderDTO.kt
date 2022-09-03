package it.polito.ticketcatalogue.dto

data class OrderDTO (
    val id: Long?,
    val quantity: Int,
    val ticket: TicketDTO,
    val price: Double,
    val status: String,
    val buyerId: Long
        )