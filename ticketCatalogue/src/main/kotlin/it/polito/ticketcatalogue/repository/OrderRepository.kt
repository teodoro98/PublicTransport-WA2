package it.polito.ticketcatalogue.repository

import it.polito.ticketcatalogue.entity.Order
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long> {

    @Transactional
    @Query(" from Order o where o.buyerId=:buyerId")
    fun findByBuyerId(@Param("buyerId")buyerId : Long): Flow<Order>

}