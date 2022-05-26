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

@RestController
class TicketCatalogueController(
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var catalogue: TicketCatalogueServiceImpl

    @GetMapping("/tickets", produces = [org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun getCatalogue(): Flow<TicketDTO> {
        return catalogue.getCatalogue()
    }

    @PostMapping("/shop/{ticketid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun purchaseTickets(@PathVariable(value="ticketid") ticketid: Long, @RequestParam requestOrder: RequestOrderDTO): Long {
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val authToken: String =
            SecurityContextHolder.getContext().getAuthentication().getCredentials().toString()
        val id : Long = userDetails.getId()

        return catalogue.purchaseTickets(id, ticketid, requestOrder)
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun getMyOrders(): Flow<OrderDTO> {
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val username : String = userDetails.username
    }

    @GetMapping("/orders/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getMyOrder(@PathVariable(value="orderId") orderId: Long): Mono<OrderDTO> {
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val username : String = userDetails.username
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/tickets")
    suspend fun addTicketsToCatalogue(@RequestBody tickets: List<TicketDTO>){
        catalogue.addTicketsToCatalogue(tickets)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders")
    fun getAllOrders(): Flux<OrderDTO> {

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders/{userId}")
    fun getOrdersOfUser(@PathVariable(value="orderId") userId: Long): Flux<OrderDTO> {

    }
}