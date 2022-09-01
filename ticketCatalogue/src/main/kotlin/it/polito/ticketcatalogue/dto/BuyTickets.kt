package it.polito.ticketcatalogue.dto

import java.sql.Timestamp

data class BuyTickets(
    val cmd: String,
    val quantity: Int,
    val zones: String,
    var type : String,
    var validitytime : Timestamp?,
    var maxnumberOfRides : Int?
)
