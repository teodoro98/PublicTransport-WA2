package it.polito.traveler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TravelerApplication

fun main(args: Array<String>) {
    runApplication<TravelerApplication>(*args)
}
