package it.polito.ticketcatalogue.entity

import it.polito.ticketcatalogue.dto.OrderDTO
import javax.persistence.*

class Order(
    @Column(updatable = false, nullable = false)
    var quantity: Int,
    @Column(updatable = false, nullable = false)
    var type: Ticket,

    @Column(updatable = false, nullable = false)
    var price: Double,
    @Column(updatable = true, nullable = false)
    var status: String,
    @Column(updatable = false, nullable = false)
    var userId: Long?,
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
        return OrderDTO(id, quantity,type,price,status,userId)
    }

    enum class Status {
        PENDING,
        SUCCESS,
        FAILURE
    }

}