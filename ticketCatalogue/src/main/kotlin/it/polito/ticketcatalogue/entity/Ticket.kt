package it.polito.ticketcatalogue.entity


import it.polito.ticketcatalogue.dto.TicketDTO
import javax.persistence.*

class Ticket(
    @Column(updatable = true, nullable = false)
    val price: Double,
    @Column(updatable = true, nullable = false)
    val type: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "user_generator")
    @SequenceGenerator(name="user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1)
    @Column(updatable = false, nullable = false)
    var ticketID : Long? = null

    fun toTicketDTO() : TicketDTO{
        return TicketDTO(ticketID, price, type)
    }
}