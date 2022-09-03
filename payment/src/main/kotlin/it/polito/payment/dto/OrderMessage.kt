package it.polito.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderMessage (
    @JsonProperty("userDetails")
    val userDetails: UserOrder,
    @JsonProperty("orderId")
    val orderId: Long,
    @JsonProperty("buyTickets")
    val buyTickets: BuyTicketsDTO,
    @JsonProperty("totalPrice")
    val totalPrice: Double,
    @JsonProperty("paymentInfo")
    val paymentInfo: PaymentInfoDTO
)