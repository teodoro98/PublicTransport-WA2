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

    override suspend fun purchaseTickets(buyerId: Long, ticketId: Long, requestOrder: RequestOrderDTO): Long {
        //TODO call travelerService API

        val authN = SecurityContextHolder.getContext().authentication
        val ticketEntity = ticketRepository.findById(ticketId)

        if(ticketEntity != null) {
            val tot = ticketEntity.price * requestOrder.quantity
            val orderEntity = orderRepository.save(
                Order(
                    requestOrder.quantity,
                    ticketEntity,
                    tot,
                    Order.Status.PENDING,
                    buyerId
                )
            )

            val order = OrderTopic(orderEntity.price, requestOrder.paymentInfo)

            //TODO Trasmission to PaymentService
            try {
                log.info("Receiving product request")
                log.info("Sending message to Kafka {}", order)
                val message: Message<OrderTopic> = MessageBuilder
                    .withPayload(order)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("X-Custom-Header", "Custom header here")
                    .build()
                kafkaTemplate.send(message)
                log.info("Message sent with success")
            } catch (e: Exception) {
                log.error("Exception: {}", e)
                // Create and handle InternalServerErrorException (Kafka)
                throw Exception()
            }

            return orderEntity.id ?: 0
        } else {
            //TODO Create and handle NoTicketFoundException
            throw Exception()
        }

    }

    override suspend fun getMyOrders(buyerId: Long): Flow<OrderDTO> {
        return orderRepository.findByBuyerId(buyerId).map { it.toOrderDTO() }
    }

    override suspend fun getMyOrder(orderID: Long): OrderDTO {
        val order = orderRepository.findById(orderID)
            ?: //TODO create and handle OrderNotFoundException
            throw Exception()
        return order.toOrderDTO()

    }

    override suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>) {
        ticketRepository.saveAll( tickets.map { Ticket(it.price,it.type)})
    }

    override suspend fun getAllOrders(): Flow<OrderDTO> {
        return orderRepository.findAll().map { it.toOrderDTO() }
    }

    override suspend fun getOrdersOfUser(buyerId: Long): Flow<OrderDTO> {
        return getMyOrders(buyerId)
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