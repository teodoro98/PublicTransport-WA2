package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.*
import it.polito.ticketcatalogue.entity.Order
import it.polito.ticketcatalogue.entity.Ticket
import it.polito.ticketcatalogue.repository.OrderRepository
import it.polito.ticketcatalogue.repository.TicketRepository
import kotlinx.coroutines.flow.map
import org.apache.kafka.common.requests.DeleteAclsResponse.log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.kafka.core.KafkaTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import org.springframework.web.reactive.function.client.*

@Service
class TicketCatalogueServiceImpl(
    @Value("\${kafka.topics.product}") val topic: String,
    @Value("\${service.traveler.uri}") val travelerServiceUri: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>
): TicketCatalogueService {

    @Autowired
    private lateinit var orderRepository :  OrderRepository
    @Autowired
    private lateinit var ticketRepository :  TicketRepository

    override suspend fun getCatalogue(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toTicketDTO() }
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

    override suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>) {
        ticketRepository.saveAll( tickets.map { Ticket(it.price,it.type)})
    }

    override fun getALlOrders(): Flux<OrderDTO> {
        TODO("Not yet implemented")
    }

    override fun getOrdersOfUser(userId: Long): Flux<OrderDTO> {
        TODO("Not yet implemented")
    }

    private suspend fun retrieveUserDetails(): UserDetailsDTO {
        val jwt: String = ""
        //TODO revise the toFLow since it's only one element
        val result = WebClient
            .create(travelerServiceUri)
            .get()
            .uri(travelerServiceUri + "/my/profile")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authentication", "Bearer: $jwt")
            .exchangeToFlow { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToFlow(UserDetailsDTO::class)
                } else {
                    emptyFlow()
                }
            }.toList()[0]
        return result
    }


}