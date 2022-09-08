package it.polito.ticketcatalogue.dto

import java.time.LocalDateTime

data class OrderDTO (
    val id: Long?,
    val quantity: Int,
    val ticket: TicketDTO,
    val price: Double,
    val status: String,
    val buyerId: Long,
    val datePurchase: LocalDateTime
        )