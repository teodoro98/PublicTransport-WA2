package it.polito.ticketcatalogue.entity

import it.polito.ticketcatalogue.dto.OrderDTO
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "order_order")
class Order(
    @Id
    var id: Long? = null,

    var quantity: Int,

    @Transient
    var ticket: Ticket?,

    var ticketId: Long,

    var price: Double,

    var status: Status,

    var buyerId: Long,

    var datePurchase: LocalDateTime
) {

    @PersistenceConstructor
    constructor(
        id: Long? = null,

        quantity: Int,

        ticketId: Long,

        price: Double,

        status: Status,

        buyerId: Long,

        datePurchase: LocalDateTime
    ) : this(id, quantity, null, ticketId, price, status, buyerId, datePurchase)


    fun toOrderDTO(): OrderDTO {
        return OrderDTO(id, quantity, ticket!!.toTicketDTO(),price,status.toString(),buyerId, datePurchase)
    }

    enum class Status {
        PENDING,
        SUCCESS,
        FAILURE
    }

}