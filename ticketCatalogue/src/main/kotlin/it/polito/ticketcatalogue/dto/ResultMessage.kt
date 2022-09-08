package it.polito.ticketcatalogue.dto

data class ResultMessage(
    val userDetails: UserOrder,
    val orderId: Long,
    val buyTickets: BuyTicketsDTO,
    val result: Boolean
)
