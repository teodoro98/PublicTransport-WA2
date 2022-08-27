package it.polito.ticketcatalogue.dto

data class RequestOrderDTO(
    val quantity: Int,
    val ticketId: Long,
    val paymentInfo: PaymentInfoDTO
)
