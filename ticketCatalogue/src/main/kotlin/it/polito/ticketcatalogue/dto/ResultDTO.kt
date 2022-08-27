package it.polito.ticketcatalogue.dto

import it.polito.ticketcatalogue.security.UserDetailsImpl

data class ResultDTO(
    val userDetails: UserOrder,
    val orderId: Long,
    val result: Boolean
)
