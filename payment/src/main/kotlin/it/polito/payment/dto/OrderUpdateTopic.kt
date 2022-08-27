package it.polito.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class Status {
    PENDING,
    SUCCESS,
    FAILURE
}

data class OrderUpdateTopic (
    @JsonProperty("orderId")
    val orderId: Long,
    @JsonProperty("orderStatus")
    val orderStatus: Status
    )