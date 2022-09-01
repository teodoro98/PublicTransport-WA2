package it.polito.ticketcatalogue.entity


import it.polito.ticketcatalogue.dto.TicketDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table(name = "ticket")
class Ticket(
    @Id
    var id : Long? = null,
    var price: Double = 0.0,
    var type: String = ""
) {
    fun toTicketDTO() : TicketDTO{
        return TicketDTO(id, price, type)
    }
}