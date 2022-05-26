package it.polito.ticketcatalogue.dto

import java.sql.Timestamp

data class TicketAcquired(
    val sub: Long?,
    val iat: Timestamp,
    val exp: Timestamp,
    val zid: String,
    val jws: String
)
