package it.polito.ticketcatalogue.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.userdetails.UserDetails

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