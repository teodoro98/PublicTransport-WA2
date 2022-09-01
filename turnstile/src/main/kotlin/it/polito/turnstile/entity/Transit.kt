package it.polito.turnstile.entity

import it.polito.turnstile.dto.TransitDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "transit")
class Transit(
    @Id
    var id : Long? = null,
    var ticketId: Long = 0,
    var turnstileId: Long = 0,
    var date: LocalDateTime= LocalDateTime.now()
){


    fun toDTO(): TransitDTO {
        return TransitDTO(id, ticketId, turnstileId, date)
    }


}