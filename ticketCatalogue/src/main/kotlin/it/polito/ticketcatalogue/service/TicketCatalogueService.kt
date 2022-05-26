package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.OrderDTO
import it.polito.ticketcatalogue.dto.TicketDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TicketCatalogueService {

    suspend fun getCatalogue() : Flow<TicketDTO>

    suspend fun purchaseTickets(buyerId: Long, ticketId: Long, requestOrder: RequestOrderDTO) : Long

    suspend fun getMyOrders(buyerId: Long) : Flow<OrderDTO>

    fun getMyOrder(username: String, orderID: Long) : Mono<OrderDTO>

    suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>)

    fun getALlOrders(): Flux<OrderDTO>

    fun getOrdersOfUser(userId: Long) : Flux<OrderDTO>
}