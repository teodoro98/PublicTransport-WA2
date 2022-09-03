package it.polito.traveler.dto

data class ResultMessage(
    val userDetails: UserOrder,
    val orderId: Long,
    val buyTickets: BuyTicketsDTO,
    val result: Boolean
)
