package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.OrderDTO
import it.polito.ticketcatalogue.dto.TicketDTO
import it.polito.ticketcatalogue.repository.OrderRepository
import it.polito.ticketcatalogue.repository.TicketRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TicketCatalogueServiceImpl(): TicketCatalogueService {

    @Autowired
    private lateinit var orderRepository :  OrderRepository
    @Autowired
    private lateinit var ticketRepository :  TicketRepository


    override fun getCatalogue(): Flux<TicketDTO> {
        return ticketRepository.findAll()
            .map { it.toTicketDTO() }
    }

    override fun purchaseTickets(username: String) {
        TODO("Not yet implemented")
    }

    override fun getMyOrders(username: String): Flux<OrderDTO> {
        TODO("Not yet implemented")
    }

    override fun getMyOrder(username: String, orderID: Long): Mono<OrderDTO> {
        TODO("Not yet implemented")
    }

    override fun addTicketsToCatalogue(tickets: List<TicketDTO>) {
        TODO("Not yet implemented")
    }

    override fun getALlOrders(): Flux<OrderDTO> {
        TODO("Not yet implemented")
    }

    override fun getOrdersOfUser(userId: Long): Flux<OrderDTO> {
        TODO("Not yet implemented")
    }


}