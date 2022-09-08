package it.polito.ticketcatalogue

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title = "Ticket Catalogue Service",
    version = "1.0",
    description = "Any authenticated user can use this service to get all the purchasable tickets and buy any of them." +
            "They're created by admins in this service"
)
)
class TicketCatalogueApplication

fun main(args: Array<String>) {
    runApplication<TicketCatalogueApplication>(*args)
}
