package it.polito.ticketcatalogue.service

import it.polito.ticketcatalogue.controller.InternalServerErrorException
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
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime

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


/*
        if(!checkUserTicketCompatible(retrieveUserDetails(userDetails), ticketEntity)) {
            throw TicketNotCompatibleException()
        }

 */

        val tot = ticketEntity.price * requestOrder.quantity
        val orderEntity = orderRepository.save(
            Order(
                null,
                requestOrder.quantity,
                ticketEntity,
                ticketEntity.id!!,
                tot,
                Order.Status.PENDING,
                buyerId,
                LocalDateTime.now()
            )
        )

        val buyTickets= BuyTicketsDTO("buy_tickets", orderEntity.quantity, ticketEntity.zone, ticketEntity.type, ticketEntity.validitytime, ticketEntity.maxnumber_of_rides  )

        val paymentInfo = requestOrder.paymentInfo

        val userOrder = UserOrder(userDetails.getId(), userDetails.username, userDetails.password, userDetails.authorities.elementAt(0)!!.authority)
        val order = OrderMessage(userOrder, orderEntity.id!!, buyTickets, orderEntity.price, paymentInfo)

        try {
            log.info("Receiving product request")
            log.info("Sending message to Kafka {}", order)
            val message: Message<OrderMessage> = MessageBuilder
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

    override suspend fun getMyOrders(buyerId: Long, since: LocalDateTime?, to: LocalDateTime?): Flow<OrderDTO> {

        return if(since == null && to != null) {
            // Only end
            orderRepository.findUserOrdersTo(buyerId, to)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }
        } else if(since != null && to == null){
            // Only beginning
            orderRepository.findUserOrdersSince(buyerId, since)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }

        } else if(since != null && to != null){
            // Both beginnig and end
            orderRepository.findUserOrdersSinceTo(buyerId, since, to)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }

        } else {
            // No period time
            orderRepository.findByBuyerId(buyerId)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }
        }
    }

    override suspend fun getMyOrder(orderID: Long): OrderDTO {
        val order = orderRepository.findById(orderID)
            ?:
            throw OrderNotFoundException()
        order.ticket = ticketRepository.findAll().filter { it.id==order.ticketId }.single()
        return order.toOrderDTO()

    }

    override suspend fun addTicketsToCatalogue(tickets: List<TicketDTO>) {

        for(t in tickets){
            val tic= Ticket(null,t.price, t.zone, t.type, t.validitytime, t.maxnumber_of_rides)
            ticketRepository.save( tic)
        }
    }


    override suspend fun modifyTicketToCatalogue(ticket: TicketDTO) {

            val tic= Ticket(ticket.ticketID,ticket.price, ticket.zone, ticket.type, ticket.validitytime, ticket.maxnumber_of_rides)
            ticketRepository.save(tic)
        }


    override suspend fun getAllOrders(since: LocalDateTime?, to: LocalDateTime?): Flow<OrderDTO> {

        return if(since == null && to != null) {
            // Only end
            orderRepository.findOrdersTo(to)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }
        } else if(since != null && to == null){
            // Only beginning
            orderRepository.findOrdersSince(since)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }

        } else if(since != null && to != null){
            // Both beginnig and end
            orderRepository.findOrdersSinceTo(since, to)
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }


        } else {
            // No period time
            orderRepository.findAll()
                .onEach { o -> o.ticket=ticketRepository.findOne(o.ticketId) }
                .map { it.toOrderDTO() }
        }

        /*return orderRepository.findAll().map {
            it.ticket = ticketRepository.findAll().filter { it2 -> it2.id==it.ticketId }.single()
            it
        }.map { it.toOrderDTO() }*/
    }

    override suspend fun getOrdersOfUser(buyerId: Long, since: LocalDateTime?, to: LocalDateTime?): Flow<OrderDTO> {
        return getMyOrders(buyerId, since, to)
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

    /*

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

     */

    override suspend fun updateOrder(userDetails: UserDetailsImpl, orderId: Long, result: Boolean){
        var status = Order.Status.FAILURE
        if(result){
            status = Order.Status.SUCCESS
        }

        val order: Order = orderRepository.findOrderById(orderId)//.filter { it.id==orderId }.single()
        order.status=status
        orderRepository.save(order)

        //val ticket = ticketRepository.findById(order.typeId)

        //val ticketstopurchase = BuyTicketsDTO("buy_tickets", order.quantity, ticket!!.zone, ticket!!.type, ticket.validitytime, ticket.maxnumber_of_rides  )

        //purchaseTicketService(userDetails, ticketstopurchase)

    }

    private suspend fun purchaseTicketService(userDetails: UserDetailsImpl, tickets: BuyTicketsDTO) {
        val jwt: String = jwtUtils.generateJwtToken(userDetails)

        val result = WebClient
            .create(travelerServiceUri)
            .post()
            .uri("$travelerServiceUri/my/tickets")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer $jwt")
            .body(Mono.just(tickets), BuyTicketsDTO::class.java)
            .retrieve()
            .awaitBody<List<TicketPurchasedDTO>>()

        println("$result")
    }


}