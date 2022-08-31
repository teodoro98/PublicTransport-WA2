package it.polito.turnstile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TurnstileApplication

fun main(args: Array<String>) {
    runApplication<TurnstileApplication>(*args)
}
