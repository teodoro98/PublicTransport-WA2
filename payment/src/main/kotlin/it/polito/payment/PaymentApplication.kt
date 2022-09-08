package it.polito.payment

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(info = Info(
    title = "Payment Service",
    version = "1.0",
    description = "Service to let perform payments towards external bank/finance systems. " +
            "It's based on an order received with internal broker system."
)
)
class PaymentApplication

fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}
