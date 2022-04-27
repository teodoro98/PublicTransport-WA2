package it.polito.server.dto

import java.util.*

data class ValidationDTO(
    val provisional_id : UUID,
    val activation_code : Long
)
