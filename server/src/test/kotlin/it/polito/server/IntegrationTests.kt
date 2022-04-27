package it.polito.server


import it.polito.server.entity.User
import it.polito.server.repository.ActivationRepository
import it.polito.server.repository.UserRepository
import it.polito.server.service.UserServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.util.Assert
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class IntegrationTests {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Test
    fun tests() {
        val baseUrl = "http://localhost:$port"

        val correctRequest = HttpEntity(User(null, "Mario", "mario@gmail.com", "Pwd123456&").toDTO())
        val duplicatedNameRequest = HttpEntity(User(null, "Mario", "mario1@gmail.com", "Pwd1234567&").toDTO())
        val duplicatedMailRequest = HttpEntity(User(null, "Mario1", "mario@gmail.com", "Pwd1234567&").toDTO())
        val invalidMailRequest = HttpEntity(User(null, "Mario1", "mariogmail.com", "Pwd1234567&").toDTO())
        val invalidPassowrdRequest1 = HttpEntity(User(null, "Carlo", "carlo@gmail.com", "Pw1&").toDTO())
        val invalidPassowrdRequest2 = HttpEntity(User(null, "Carlo", "carlo@gmail.com", "P344234231&").toDTO())
        val invalidPassowrdRequest3 = HttpEntity(User(null, "Carlo", "carlo@gmail.com", "Pwdsasdsadsa&").toDTO())
        val invalidPassowrdRequest4 = HttpEntity(User(null, "Carlo", "carlo@gmail.com", "Pw1111111111").toDTO())
        val invalidPassowrdRequest5 = HttpEntity(User(null, "Carlo", "carlo@gmail.com", "fdsfdsfdsw1&").toDTO())

        //Correct case
        var response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            correctRequest
        )
        assert(response.statusCode == HttpStatus.CREATED)
        println(response.body)

        //Duplicated name
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            duplicatedNameRequest
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

        //Duplicated mail
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            duplicatedMailRequest
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

        //Invalid mail
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            invalidMailRequest
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

        //Check password
        //Short pwd
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            invalidPassowrdRequest1
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No lower case
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            invalidPassowrdRequest2
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No digit
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            invalidPassowrdRequest3
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No special
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            invalidPassowrdRequest4
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No upper case
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            invalidPassowrdRequest5
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

    }

    @Test
    fun pruneExpire() {
        val baseUrl = "http://localhost:$port"
        val correctRequest = HttpEntity(User(null, "Mario", "mario@gmail.com", "Pwd123456&").toDTO())
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/register",
            correctRequest
        )
        println(response.toString())
        println(response.statusCode)
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)

        Thread.sleep(10000)
        println(userRepository.findAll().count())
        println(activationRepository.findAll())
        Assertions.assertEquals(0,userRepository.findAll().count())
        Assertions.assertEquals(0,activationRepository.findAll().count())

    }

    //Todo test counter
}

