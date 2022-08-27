package it.polito.traveler.repository

import it.polito.traveler.entity.TicketPurchased
import org.springframework.data.repository.CrudRepository

interface TicketPurchasedRepository: CrudRepository<TicketPurchased,Long> {
}