package it.polito.ticketcatalogue.dto

import java.sql.Timestamp

data class TicketDTO(
    val ticketID: Long?,
    val price: Double,
    val zone: String,
    val type: String,
    var validitytime : Timestamp?,
    val maxnumber_of_rides : Int?
)
