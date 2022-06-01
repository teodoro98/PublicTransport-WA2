package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.dto.*
import it.polito.ticketcatalogue.entity.Order
import it.polito.ticketcatalogue.entity.Ticket
import it.polito.ticketcatalogue.repository.OrderRepository
import it.polito.ticketcatalogue.repository.TicketRepository
import it.polito.ticketcatalogue.security.JwtUtils
import it.polito.ticketcatalogue.security.UserDetailsImpl
import kotlinx.coroutines.flow.*
import org.apache.kafka.common.requests.DeleteAclsResponse.log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.reactive.function.client.*
import java.time.LocalDate

@Service
class TicketCatalogueServiceImpl(
    @Value("\${kafka.topics.product}") val topic: String,
    @Value("\${service.traveler.uri}") val travelerServiceUri: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Autowired
    private val jwtUtils: JwtUtils
): TicketCatalogueService {

    @Autowired
    private lateinit var orderRepository :  OrderRepository
    @Autowired
    private lateinit var ticketRepository :  TicketRepository

    override suspend fun getCatalogue(): Flow<TicketDTO> {
        return ticketRepository.findAll().map { it.toTicketDTO() }
    }

    override suspend fun purchaseTickets(userDetails: UserDetailsImpl, ticketId: Long, requestOrder: RequestOrderDTO): Long {
        //val ticketEntity = ticketRepository.findOne(ticketId)
        //val ticketEntity = ticketRepository.findById(ticketId)

        val ticketEntity = ticketRepository.findAll().filter { it.id == ticketId }.single()

        val buyerId = userDetails.getId()

        if(ticketEntity != null) {

            if(!checkUserTicketCompatible(retrieveUserDetails(userDetails), ticketEntity)) {
                //TODO handle and manage TicketNotCompatibleException()
                throw TicketNotCompatibleException()
            }

            val tot = ticketEntity.price * requestOrder.quantity
            val orderEntity = orderRepository.save(
                Order(
                    null,
                    requestOrder.quantity,
                    null,
                    ticketEntity.id!!,
                    tot,
                    Order.Status.PENDING,
                    buyerId
                )
            )

            val paymentInfo = requestOrder.paymentInfo

            val order = OrderTopic(orderEntity.price, paymentInfo)

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
                //TODO Create and handle InternalServerErrorException (Kafka)
                throw InternalServerErrorException()
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

    private suspend fun checkUserTicketCompatible(user: UserDetailsDTO, ticket: Ticket): Boolean {
        return when (ticket.type) {
            "young" -> {
                // <18
                user.dateOfBirth.plusYears(18).isBefore(LocalDate.now())
            }
            "elder" -> {
                //>65
                user.dateOfBirth.plusYears(65).isAfter(LocalDate.now())

            }
            else -> {
                true
            }
        }
    }

    private suspend fun retrieveUserDetails(userDetails: UserDetailsImpl): UserDetailsDTO {
        val jwt: String = jwtUtils.generateJwtToken(userDetails)
        //TODO revise the toFLow since it's only one element
        val result = WebClient
            .create(travelerServiceUri)
            .get()
            .uri("$travelerServiceUri/my/profile")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $jwt")
            .retrieve()
            .awaitBody<UserDetailsDTO>()

            /*.exchangeToFlow { response ->
                if (response.statusCode() == HttpStatus.OK) {
                    response.bodyToFlow(UserDetailsDTO::class)
                } else {
                    emptyFlow()
                }
            }.single()*/
        println("$result")
        return result
    }


}