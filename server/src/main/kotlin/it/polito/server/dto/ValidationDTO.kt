package it.polito.server.dto

import java.util.*

data class ValidationDTO(
    val provisionalID : UUID,
    val activation_code : Long
)
