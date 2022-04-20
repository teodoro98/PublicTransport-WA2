package it.polito.server.dto

import it.polito.server.entity.Activation
import it.polito.server.entity.User
import java.util.UUID

data class ActivationDTO(
    val id:UUID?,
    val counter:Long,
    val user:User
)

fun Activation.toDTO(): ActivationDTO{
    return ActivationDTO(id, counter, user)
}
