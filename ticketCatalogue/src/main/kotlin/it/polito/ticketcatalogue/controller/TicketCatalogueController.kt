package it.polito.ticketcatalogue.controller

import it.polito.ticketcatalogue.dto.OrderCommittedDTO
import it.polito.ticketcatalogue.dto.OrderDTO
import it.polito.ticketcatalogue.dto.RequestOrderDTO
import it.polito.ticketcatalogue.dto.TicketDTO
import it.polito.ticketcatalogue.entity.Ticket
import it.polito.ticketcatalogue.security.UserDetailsImpl
import it.polito.ticketcatalogue.service.TicketCatalogueServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class TicketCatalogueController {

    @Autowired
    private lateinit var catalogue: TicketCatalogueServiceImpl

    @GetMapping("/tickets")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getCatalogue(): Flux<TicketDTO> {
        return catalogue.getCatalogue()
    }

    @PostMapping("/shop/{ticketid}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun purchaseTickets(@PathVariable(value="ticketid") ticketid: Long, @RequestParam requestOrder: RequestOrderDTO): Mono<OrderCommittedDTO> {
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val username : String = userDetails.username

        // Trasmission to PaymentService
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun getMyOrders(): Flux<OrderDTO> {
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
    fun addTicketsToCatalogue(@RequestBody tickets: List<TicketDTO>){

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