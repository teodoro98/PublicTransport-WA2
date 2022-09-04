package it.polito.ticketcatalogue.dto

data class RequestOrderDTO(
    val quantity: Int,
    val paymentInfo: PaymentInfoDTO
)
