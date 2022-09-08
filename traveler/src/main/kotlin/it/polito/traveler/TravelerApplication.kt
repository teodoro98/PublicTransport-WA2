package it.polito.traveler

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title = "Traveler Service",
    version = "1.0",
    description = "This service can return any information of any travelers, personal data, bought tickets. " +
            "Customers can only see their infos, while admins could have global view."
)
)
class TravelerApplication

fun main(args: Array<String>) {
    runApplication<TravelerApplication>(*args)
}
