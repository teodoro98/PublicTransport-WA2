package it.polito.turnstile.entity

import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.dto.TurnstileDetailsDTO
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Id


@Table(name = "turnstile_details")
class TurnstileDetatils(
    @Id
    var id : Long? = null,
    var username : String = "",
    var zoneId : String = ""
){

    fun toDTO(): TurnstileDetailsDTO {
        return TurnstileDetailsDTO(id, username, zoneId)
    }

}