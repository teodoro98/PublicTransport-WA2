package it.polito.traveler.dto

import java.sql.Timestamp

data class TicketPurchasedDTO (
    val sub: Long?,
    val iat: Timestamp,
    val exp: Timestamp,
    val zid: String
)