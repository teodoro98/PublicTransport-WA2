package it.polito.login

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.*
import org.springframework.data.jpa.repository.config.*
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan(basePackages = ["it.polito.login.entity"])
@EnableJpaRepositories(basePackages = ["it.polito.login.repository"])
@OpenAPIDefinition(info = Info(
    title = "Login Service",
    version = "1.0",
    description = "Service to let customer, admin, admin recruiter and turnstile to register and login. " +
            "The service implements the role of AuthN and AuthZ server"
)
)
class LoginApplication

fun main(args: Array<String>) {
    runApplication<LoginApplication>(*args)
}
