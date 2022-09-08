package it.polito.turnstile

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title = "Turnstile Service",
    version = "1.0",
    description = "This service can be used only by admins and turnstile. " +
            "It permits the turnstiles to check the tickets and their validity, " +
            "meanwhile reporting usage of them to admins."
)
)
class TurnstileApplication

fun main(args: Array<String>) {
    runApplication<TurnstileApplication>(*args)
}
