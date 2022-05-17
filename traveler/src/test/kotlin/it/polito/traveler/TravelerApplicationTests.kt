package it.polito.traveler

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.TicketPurchased
import it.polito.traveler.entity.UserDetails
import it.polito.traveler.repository.TicketPurchasedRepository
import it.polito.traveler.repository.UserDetailsRepository
import net.bytebuddy.utility.dispatcher.JavaDispatcher
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
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
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: UserDetailsRepository

    @BeforeEach
    fun clearDb() {
        userDetailsRepository.deleteAll()
        println("Deleted users details")
        ticketPurchasedRepository.deleteAll()
        println("Deleted tickets")
    }

    @Test
    fun testsAPI() {
        val baseUrl = "http://localhost:$port"

        val userDetails = UserDetails("Mario", "Via Rossi", LocalDate.now(), 3452432432)
        val updatedUserDetails = UserDetails("Franco", "Franconi", LocalDate.now(), 3452432432)
        userDetailsRepository.save(userDetails)

        //TODO id deve essere preso dalla security
        val idLong = userDetailsRepository.findIdByName("Mario")!!
        val id = HttpEntity(idLong)


        //Test get profile correct case
        //var response = restTemplate.getForEntity<Unit>(
        //     "$baseUrl/my/getProfile/$id", UserDetailsDTO::javaClass, id

        //)

        // PROFILE
        //----------------------------------------------------------------------------------

        var responseGetProfile = restTemplate.getForEntity<UserDetailsDTO>(
            "$baseUrl/my/profile",
            id
        )
        Assertions.assertEquals(responseGetProfile.statusCode, HttpStatus.FOUND)

        //Test put profile correct case
        var responsePutProfile = restTemplate.put(
            "$baseUrl/my/profile",
            updatedUserDetails
                   )
        Assertions.assertEquals(responsePutProfile.statusCode, HttpStatus.ACCEPTED)

        // TICKETS
        //----------------------------------------------------------------------------------

        //Test get tickets correct case
        var responseGetTickets = restTemplate.getForEntity<List<TicketPurchasedDTO>>(
            "$baseUrl/my/tickets", id
        )
        Assertions.assertEquals(responseGetTickets.statusCode, HttpStatus.FOUND)

        // TODO bisogna passare un payload con {cmd: "buy_tickets", quantity: 2, zones: "ABC"}
        var responsePostTickets = restTemplate.postForEntity<Unit>(
            "$baseUrl/my/tickets",
            id
        )
        Assertions.assertEquals(responsePostTickets.statusCode, HttpStatus.ACCEPTED)

        // ADMIN
        //----------------------------------------------------------------------------------
        var responseAdminTravelers = restTemplate.getForEntity<List<UserDetailsDTO>>(
            "$baseUrl/admin/travelers"
        )
        Assertions.assertEquals(responseAdminTravelers.statusCode, HttpStatus.FOUND)

        var responseAdminProfile = restTemplate.getForEntity<List<UserDetailsDTO>>(
            "$baseUrl/admin/traveler/$idLong/profile"
        )
        Assertions.assertEquals(responseAdminProfile.statusCode, HttpStatus.FOUND)

        var responseAdminTickets = restTemplate.getForEntity<List<TicketPurchasedDTO>>(
            "$baseUrl/admin/traveler/$idLong/tickets"
        )
        Assertions.assertEquals(responseAdminTickets.statusCode, HttpStatus.FOUND)
    }
}
