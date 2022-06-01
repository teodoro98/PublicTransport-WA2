package it.polito.server

import it.polito.server.service.EmailServiceImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.*
import org.springframework.data.jpa.repository.config.*
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan(basePackages = ["it.polito.server.entity"])
@EnableJpaRepositories(basePackages = ["it.polito.server.repository"])
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)

}
