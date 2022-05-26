package it.polito.ticketcatalogue.entity

import it.polito.ticketcatalogue.dto.OrderDTO
import javax.persistence.*

class Order(
    @Column(updatable = false, nullable = false)
    var quantity: Int,

    @ManyToOne
    @JoinColumn(name = "ticket", referencedColumnName = "type")
    @Column(updatable = false, nullable = false)
    var type: Ticket,

    @Column(updatable = false, nullable = false)
    var price: Double,

    @Column(updatable = true, nullable = false)
    var status: Status,

    @Column(updatable = false, nullable = false)
    var buyerId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "user_generator")
    @SequenceGenerator(name="user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1)
    @Column(updatable = false, nullable = false)
    val id: Long? = null

    fun toOrderDTO(): OrderDTO {
        return OrderDTO(id, quantity, type.type,price,status.toString(),buyerId)
    }

    enum class Status {
        PENDING,
        SUCCESS,
        FAILURE
    }

}