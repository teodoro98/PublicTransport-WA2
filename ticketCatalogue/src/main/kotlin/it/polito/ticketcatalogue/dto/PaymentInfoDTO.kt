package it.polito.ticketcatalogue.dto

import java.time.LocalDate

data class PaymentInfoDTO(
    val creditCardNumber: String,
    val exp: String,
    val cvv: String,
    val cardHolder: String
)
