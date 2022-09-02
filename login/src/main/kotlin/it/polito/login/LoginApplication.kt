package it.polito.login

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.*
import org.springframework.data.jpa.repository.config.*
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan(basePackages = ["it.polito.login.entity"])
@EnableJpaRepositories(basePackages = ["it.polito.login.repository"])
class LoginApplication

fun main(args: Array<String>) {
    runApplication<LoginApplication>(*args)
}
