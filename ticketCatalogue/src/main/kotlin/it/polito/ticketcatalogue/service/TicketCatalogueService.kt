package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.OrderDTO
import it.polito.ticketcatalogue.dto.RequestOrderDTO
import it.polito.ticketcatalogue.dto.TicketDTO
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TicketCatalogueService {

    suspend fun getCatalogue() : Flow<TicketDTO>

    suspend fun purchaseTickets(buyerId: Long, ticketId: Long, requestOrder: RequestOrderDTO) : Long

    suspend fun getMyOrders(buyerId: Long) : Flow<OrderDTO>

    suspend fun getMyOrder(orderID: Long) : OrderDTO

    suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>)

    suspend fun getAllOrders(): Flow<OrderDTO>

    suspend fun getOrdersOfUser(buyerId: Long) : Flow<OrderDTO>
}