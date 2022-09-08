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
    var zone: String = "",
    var type: String = "", //TYPE travelcard, carnet
    var validitytime :Timestamp? = null,
    val maxnumber_of_rides :Int? = 0
) {
    fun toTicketDTO() : TicketDTO{
        return TicketDTO(id, price, zone, type, validitytime, maxnumber_of_rides)
    }
}