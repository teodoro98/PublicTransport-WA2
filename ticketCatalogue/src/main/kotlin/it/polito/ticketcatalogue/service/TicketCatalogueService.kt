package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.OrderDTO
import it.polito.ticketcatalogue.dto.TicketDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TicketCatalogueService {

    fun getCatalogue() : Flux<TicketDTO>

    fun purchaseTickets(username: String)

    fun getMyOrders(username: String) : Flux<OrderDTO>

    fun getMyOrder(username: String, orderID: Long) : Mono<OrderDTO>

    fun addTicketsToCatalogue(tickets: List<TicketDTO>)

    fun getALlOrders(): Flux<OrderDTO>

    fun getOrdersOfUser(userId: Long) : Flux<OrderDTO>
}