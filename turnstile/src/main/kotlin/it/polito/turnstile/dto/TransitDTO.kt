package it.polito.turnstile.dto

import java.time.LocalDateTime

data class TransitDTO (
    val transitId : Long?,
    val ticketId : Long,
    val user: String,
    val turnstileUsername : String,
    val transitDate : LocalDateTime
)