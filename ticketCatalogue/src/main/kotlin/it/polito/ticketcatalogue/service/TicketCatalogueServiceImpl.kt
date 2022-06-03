package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.controller.InternalServerErrorException
import it.polito.ticketcatalogue.controller.NoTicketFoundException
import it.polito.ticketcatalogue.controller.OrderNotFoundException
import it.polito.ticketcatalogue.controller.TicketNotCompatibleException
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
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
        val ticketEntities = ticketRepository.findAll()
        return ticketRepository.findAll().map { it.toTicketDTO() }
    }

    override suspend fun purchaseTickets(userDetails: UserDetailsImpl, ticketId: Long, requestOrder: RequestOrderDTO): Long {
        //val ticketEntity = ticketRepository.findOne(ticketId)
        //val ticketEntity = ticketRepository.findById(ticketId)

        val ticketEntity = ticketRepository.findAll().filter { it.id == ticketId }.single()

        val buyerId = userDetails.getId()



        if(!checkUserTicketCompatible(retrieveUserDetails(userDetails), ticketEntity)) {
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

        val userOrder = UserOrder(userDetails.getId(), userDetails.username, userDetails.password, userDetails.authorities.elementAt(0)!!.authority)
        val order = OrderTopic(userOrder, orderEntity.id!!, orderEntity.price, paymentInfo)

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
            throw InternalServerErrorException()
        }

        return orderEntity.id ?: 0

    }

    override suspend fun getMyOrders(buyerId: Long): Flow<OrderDTO> {
        return orderRepository.findByBuyerId(buyerId).map {
            it.type = ticketRepository.findAll().filter { it2 -> it2.id==it.typeId }.single()
            it
         }.map { it.toOrderDTO() }
    }

    override suspend fun getMyOrder(orderID: Long): OrderDTO {
        val order = orderRepository.findById(orderID)
            ?:
            throw OrderNotFoundException()
        order.type = ticketRepository.findAll().filter { it.id==order.typeId }.single()
        return order.toOrderDTO()

    }

    override suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>) {

        for(t in tickets){
            val tic= Ticket(null,t.price,t.type)
            ticketRepository.save( tic)
        }
    }

    override suspend fun getAllOrders(): Flow<OrderDTO> {
        return orderRepository.findAll().map {
            it.type = ticketRepository.findAll().filter { it2 -> it2.id==it.typeId }.single()
            it
        }.map { it.toOrderDTO() }
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
        val result = WebClient
            .create(travelerServiceUri)
            .get()
            .uri("$travelerServiceUri/my/profile")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer $jwt")
            .retrieve()
            .awaitBody<UserDetailsDTO>()
        println("$result")
        return result
    }

    override suspend fun updateOrder(userDetails: UserDetailsImpl, orderId: Long, result: Boolean){
        var status = Order.Status.FAILURE
        if(result){
            status = Order.Status.SUCCESS
        }

        val order: Order = orderRepository.findOrderById(orderId)//.filter { it.id==orderId }.single()
        order.status=status
        orderRepository.save(order)


        val zones = "ABC"
        val tickets = BuyTickets("buy_tickets", order.quantity, zones)

        purchaseTicketService(userDetails, tickets)

    }

    private suspend fun purchaseTicketService(userDetails: UserDetailsImpl, tickets: BuyTickets) {
        val jwt: String = jwtUtils.generateJwtToken(userDetails)

        val result = WebClient
            .create(travelerServiceUri)
            .post()
            .uri("$travelerServiceUri/my/tickets")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer $jwt")
            .body(Mono.just(tickets), BuyTickets::class.java)
            .retrieve()
            .awaitBody<List<TicketPurchasedDTO>>()

        println("$result")
    }


}