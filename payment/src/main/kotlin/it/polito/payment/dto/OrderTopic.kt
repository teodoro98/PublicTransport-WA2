package it.polito.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty
import it.polito.payment.security.UserDetailsImpl

data class OrderTopic (
    @JsonProperty("userDetails")
    val userDetails: UserOrder,
    @JsonProperty("orderId")
    val orderId: Long,
    @JsonProperty("totalPrice")
    val totalPrice: Double,
    @JsonProperty("paymentInfo")
    val paymentInfo: PaymentInfoDTO
)