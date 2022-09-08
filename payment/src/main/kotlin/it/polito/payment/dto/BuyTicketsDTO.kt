package it.polito.payment.dto

import java.sql.Timestamp

data class BuyTicketsDTO(
    val cmd: String,
    val quantity: Int,
    val zones: String,
    var type : String,
    var validitytime : Timestamp?,
    var maxnumberOfRides : Int?
)
