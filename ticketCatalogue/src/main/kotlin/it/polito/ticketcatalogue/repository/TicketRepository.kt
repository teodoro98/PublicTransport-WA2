package it.polito.ticketcatalogue.repository

import it.polito.ticketcatalogue.entity.Ticket
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TicketRepository : ReactiveCrudRepository<Ticket,Long>{
}