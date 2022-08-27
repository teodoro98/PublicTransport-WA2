package it.polito.ticketcatalogue.controller

import it.polito.ticketcatalogue.dto.*
import it.polito.ticketcatalogue.entity.Order
import it.polito.ticketcatalogue.entity.Ticket
import it.polito.ticketcatalogue.repository.TicketRepository
import it.polito.ticketcatalogue.security.UserDetailsImpl
import it.polito.ticketcatalogue.service.TicketCatalogueServiceImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import kotlinx.coroutines.flow.Flow
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.security.Principal

@RestController
class TicketCatalogueController(
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var catalogue: TicketCatalogueServiceImpl

    @Autowired
    private lateinit var ticketRepository : TicketRepository

    @GetMapping("/tickets", produces = [org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun getCatalogue(): Flow<TicketDTO> {
        return catalogue.getCatalogue()
    }

    @PostMapping("/shop/{ticketid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun purchaseTickets(@PathVariable("ticketid") ticketid: Long, @RequestBody requestOrder: RequestOrderDTO, principal: Principal): Long {
        val userDetails: UserDetailsImpl = (principal as UsernamePasswordAuthenticationToken).principal as UserDetailsImpl

        return catalogue.purchaseTickets(userDetails, ticketid, requestOrder)
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun getMyOrders(principal: Principal): Flow<OrderDTO> {
        val userDetails: UserDetailsImpl = (principal as UsernamePasswordAuthenticationToken).principal as UserDetailsImpl
        val buyerId : Long = userDetails.getId()
        return catalogue.getMyOrders(buyerId)
    }

    @GetMapping("/orders/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun getMyOrder(@PathVariable(value="orderId") orderId: Long, principal: Principal): OrderDTO {
        val userDetails: UserDetailsImpl = (principal as UsernamePasswordAuthenticationToken).principal as UserDetailsImpl
        val order = catalogue.getMyOrder(orderId)
        if(order.buyerId != userDetails.getId()) {
            throw OrderNotFoundException()
        }
        return order
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/tickets")
    suspend fun addTicketsToCatalogue(@RequestBody tickets: List<TicketDTO>){
        catalogue.addTicketsToCatalogue(tickets)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders")
    suspend fun getAllOrders(): Flow<OrderDTO> {
        return catalogue.getAllOrders()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders/{user-id}")
    suspend fun getOrdersOfUser(@PathVariable(value="user-id") buyerId: Long): Flow<OrderDTO> {
        return catalogue.getOrdersOfUser(buyerId)

    }
}