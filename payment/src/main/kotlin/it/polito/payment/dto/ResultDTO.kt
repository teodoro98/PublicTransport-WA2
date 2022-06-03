package it.polito.payment.dto

import it.polito.payment.security.UserDetailsImpl

data class ResultDTO(
    val userDetails: UserOrder,
    val orderId: Long,
    val result: Boolean
)
