package it.polito.ticketcatalogue.repository

import it.polito.ticketcatalogue.entity.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long> {

    @Transactional
    @Query("SELECT * from order_order as o, ticket WHERE o.ticket_id = ticket.id AND o.buyer_id=:buyerId")
    suspend fun findByBuyerId(@Param("buyerId")buyerId : Long): Flow<Order>


    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.id=:orderID")
    suspend fun findOrderById(@Param("orderID")orderID : Long): Order


    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.date_purchase >=:since AND order_order.buyer_id=:buyerId")
    suspend fun findUserOrdersSince(@Param("buyerId")buyerId: Long, @Param("since")since : LocalDateTime): Flow<Order>

    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.date_purchase <=:to AND order_order.buyer_id=:buyerId")
    suspend fun findUserOrdersTo(@Param("buyerId")buyerId: Long, @Param("to")to : LocalDateTime): Flow<Order>

    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.date_purchase >=:since AND order_order.date_purchase <=:to AND order_order.buyer_id=:buyerId")
    suspend fun findUserOrdersSinceTo(@Param("buyerId")buyerId: Long, @Param("since")since : LocalDateTime, @Param("to")to : LocalDateTime): Flow<Order>

    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.date_purchase >=:since")
    suspend fun findOrdersSince( @Param("since")since : LocalDateTime): Flow<Order>

    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.date_purchase <=:to")
    suspend fun findOrdersTo( @Param("to")to : LocalDateTime): Flow<Order>

    @Query("SELECT * FROM order_order, ticket WHERE order_order.ticket_id = ticket.id AND order_order.date_purchase >=:since AND order_order.date_purchase <=:to")
    suspend fun findOrdersSinceTo( @Param("since")since : LocalDateTime, @Param("to")to : LocalDateTime): Flow<Order>

}