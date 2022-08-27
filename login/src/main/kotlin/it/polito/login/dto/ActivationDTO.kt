package it.polito.login.dto

import it.polito.login.entity.User
import java.time.LocalDateTime
import java.util.UUID

data class ActivationDTO(
    val id:UUID?,
    val counter:Long,
    val user:User,
    val token: Long,
    val deadline: LocalDateTime
)