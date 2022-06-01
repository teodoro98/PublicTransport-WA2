package it.polito.payment.dto

import java.time.LocalDate

data class PaymentInfoDTO(
    val creditCardNumber: String,
    val exp: LocalDate,
    val cvv: String,
    val cardHolder: String
)
