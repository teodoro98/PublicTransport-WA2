package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.OrderDTO
import it.polito.ticketcatalogue.dto.RequestOrderDTO
import it.polito.ticketcatalogue.dto.TicketDTO
import it.polito.ticketcatalogue.security.UserDetailsImpl
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface TicketCatalogueService {

    suspend fun getCatalogue() : Flow<TicketDTO>

    suspend fun purchaseTickets(userDetails: UserDetailsImpl, ticketId: Long, requestOrder: RequestOrderDTO) : Long

    suspend fun getMyOrders(buyerId: Long, since: LocalDateTime?, to: LocalDateTime?) : Flow<OrderDTO>

    suspend fun getMyOrder(orderID: Long) : OrderDTO

    suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>)

    suspend fun modifyTicketToCatalogue(ticket: TicketDTO)

    suspend fun getAllOrders(since: LocalDateTime?, to: LocalDateTime?): Flow<OrderDTO>

    suspend fun getOrdersOfUser(buyerId: Long, since: LocalDateTime?, to: LocalDateTime?) : Flow<OrderDTO>

    suspend fun updateOrder(userDetails: UserDetailsImpl, orderId: Long, result: Boolean)
}