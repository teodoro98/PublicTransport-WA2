package it.polito.ticketcatalogue.repository

import it.polito.ticketcatalogue.entity.Order
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrderRepository: ReactiveCrudRepository<Order, Long> {
}