package it.polito.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderTopic (
    @JsonProperty("totalPrice")
    val totalPrice: Double,
    @JsonProperty("paymentInfo")
    val paymentInfo: PaymentInfoDTO
)