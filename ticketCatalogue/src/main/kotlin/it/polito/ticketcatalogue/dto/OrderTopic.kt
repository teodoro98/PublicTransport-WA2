package it.polito.ticketcatalogue.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderTopic (
    @JsonProperty("totalPrice")
    val totalPrice: Double,
    @JsonProperty("paymentInfo")
    val paymentInfo: PaymentInfoDTO
)