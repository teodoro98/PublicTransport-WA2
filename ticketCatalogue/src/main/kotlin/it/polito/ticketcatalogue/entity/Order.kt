package it.polito.ticketcatalogue.entity

import it.polito.ticketcatalogue.dto.OrderDTO
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table

// TODO aggiungere lista di ticket acquistati
@Table(name = "order_order")
class Order(
    @Id
    var id: Long? = null,

    var quantity: Int,

    @Transient
    var type: Ticket?,

    var typeId: Long,

    var price: Double,

    var status: Status,

    var buyerId: Long
) {

    @PersistenceConstructor
    constructor(
        id: Long? = null,

        quantity: Int,

        typeId: Long,

        price: Double,

        status: Status,

        buyerId: Long
    ) : this(id, quantity, null, typeId, price, status, buyerId)


    fun toOrderDTO(): OrderDTO {
        return OrderDTO(id, quantity, type!!.type,price,status.toString(),buyerId)
    }

    enum class Status {
        PENDING,
        SUCCESS,
        FAILURE
    }

}