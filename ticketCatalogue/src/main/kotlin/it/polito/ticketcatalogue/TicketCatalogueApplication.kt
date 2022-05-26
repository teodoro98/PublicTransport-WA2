package it.polito.ticketcatalogue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TicketCatalogueApplication

fun main(args: Array<String>) {
    runApplication<TicketCatalogueApplication>(*args)
}
