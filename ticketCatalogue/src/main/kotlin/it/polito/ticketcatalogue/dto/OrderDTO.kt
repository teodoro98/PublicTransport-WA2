package it.polito.ticketcatalogue.dto

data class OrderDTO (
    val id: Long?,
    val quantity: Int,
    val type: String,
    val price: Double,
    val status: String,
    val userId: Long?
        )