package it.polito.server


import it.polito.server.dto.UserDTO
import it.polito.server.dto.UserSlimDTO
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
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random


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
            "$baseUrl/users/register",
            correctRequest
        )
        assert(response.statusCode == HttpStatus.CREATED)
        println(response.body)

        //Duplicated name
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            duplicatedNameRequest
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

        //Duplicated mail
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            duplicatedMailRequest
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

        //Invalid mail
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            invalidMailRequest
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

        //Check password
        //Short pwd
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            invalidPassowrdRequest1
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No lower case
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            invalidPassowrdRequest2
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No digit
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            invalidPassowrdRequest3
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No special
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            invalidPassowrdRequest4
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        //No upper case
        response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
            invalidPassowrdRequest5
        )
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

    }

    @Test
    fun pruneExpire() {
        val baseUrl = "http://localhost:$port"
        val correctRequest = HttpEntity(User(null, "Mario", "mario@gmail.com", "Pwd123456&").toDTO())
        val response = restTemplate.postForEntity<Unit>(
            "$baseUrl/users/register",
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

    @Test
    fun rateLimiter() {
        val baseUrl = "http://localhost:$port"
       /* for(i in 0..20) {
            //val name = Name.values()[Random.nextInt(Name.values().size)].toString()
            val name = Name.values()[i].toString()
            val email = name.lowercase().plus("@gmail.com")
            val userdto = User(null, name, email, "Pwd123456&").toDTO()
            println("User: $userdto")
            val correctRequest = HttpEntity(userdto)
            val response = restTemplate.postForEntity<Unit>(
                "$baseUrl/users/register",
                correctRequest
            )
            println("Request $i at ${LocalDateTime.now()} with Response: ${response.statusCode}")
        }*/
        var cycle = true
        var counter = 0
        var start : LocalTime  = LocalTime.now()
        while(cycle) {
            if(counter == 0) start = LocalTime.now()
            val name = Name.values()[Random.nextInt(Name.values().size)].toString()
            val email = name.lowercase().plus("@gmail.com")
            val userdto = User(null, name, email, "Pwd123456&").toDTO()
            val correctRequest = HttpEntity(userdto)
            counter++
            val response = restTemplate.postForEntity<Unit>(
                "$baseUrl/users/register",
                correctRequest
            )

            if(start.until(LocalTime.now(), ChronoUnit.SECONDS) == 1L) {
                if(counter >= 10) {
                    Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.statusCode)
                    cycle = false
                } else {
                    counter = 0
                    println("Counter resetted")
                }
            }
        }
    }

    //Todo test counter
}

enum class Name {
    Mario,
    Andrea,
    Lorenzo,
    Roberto,
    Teodoro,
    Anna,
    Federico,
    Francesco,
    Gabriele,
    Giovanni,
    Gianpiero,
    Marco,
    Cristina,
    Michela,
    Giulia,
    Valentina,
    Matteo,
    Simona,
    Katia,
    Sabrina,
    Maria,
    Hinata,
    Sakura
}

