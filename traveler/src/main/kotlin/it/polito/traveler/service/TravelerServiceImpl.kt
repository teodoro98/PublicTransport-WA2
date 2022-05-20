package it.polito.traveler.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.traveler.controller.UserDetailsNotFoundException
import it.polito.traveler.controller.UserEmpty
import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.TicketPurchased
import it.polito.traveler.entity.UserDetails
import it.polito.traveler.repository.TicketPurchasedRepository
import it.polito.traveler.repository.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.sql.Timestamp
import java.time.LocalDate
import java.util.*
import javax.crypto.KeyGenerator


@Service
class TravelerServiceImpl(@Value("\${server.ticket.token.secret}") val ticketSecret: String
):TravelerService {

    // Repositories
    @Autowired
    private lateinit var userDetailsRepository :  UserDetailsRepository
    @Autowired
    private lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    override fun getProfile(username:String): UserDetailsDTO {
        val id = userDetailsRepository.findIdByUsername(username) ?: throw UserDetailsNotFoundException()
        val user = userDetailsRepository.findById(id!!).get()

        return user.toDTOUserDetails()
    }

    override fun updateProfile(userDetailsDTO: UserDetailsDTO, username : String) {
        var user: UserDetails
        try {
            user = userDetailsRepository.findById(userDetailsDTO.id!!).get()
        }catch (e : Exception){
            throw UserEmpty()
        }
        user.address= userDetailsDTO.address
        user.name= userDetailsDTO.name
        user.telephoneNumber= userDetailsDTO.telephoneNumber
        user.dateOfBirth = userDetailsDTO.dateOfBirth
        userDetailsRepository.save(user)
    
    }

    override fun getTickets(username : String) : List<TicketPurchasedDTO> {
        val listTickets = mutableListOf<TicketPurchasedDTO>()
        val id: Long = userDetailsRepository.findIdByUsername(username)?: throw UserDetailsNotFoundException()
        val user: UserDetails
        try {
            user = userDetailsRepository.findById(id).get()
        }catch (e : Exception){
            throw UserEmpty()
        }
        val tickets = userDetailsRepository.findTicketsByUser(user)
        for (t in tickets!!){
            listTickets.add(TicketPurchasedDTO(t.id, t.issuedAt, t.expiry, t.zoneID,
                createTicketJwt(t.id.toString(), t.issuedAt, t.expiry, t.zoneID)
            ))
        }
        return listTickets
    }

    override fun buyTickets(username: String, quantity: Int, zones: String) : MutableList<TicketPurchasedDTO>{
        val purchasedTickets = mutableListOf<TicketPurchasedDTO>()
        val id: Long = userDetailsRepository.findIdByUsername(username)?: throw UserDetailsNotFoundException()
        val user: UserDetails
        try {
            user = userDetailsRepository.findById(id).get()
        }catch (e : Exception){
            throw UserEmpty()
        }


        repeat(quantity){
            val purchasedTicket = TicketPurchased(
                user,
                java.sql.Timestamp(System.currentTimeMillis()),
                java.sql.Timestamp(System.currentTimeMillis() + 3_600 * 1_000),
                zones
            )
            ticketPurchasedRepository.save(purchasedTicket)
            user.addTicket(purchasedTicket)
            userDetailsRepository.save(user)

            purchasedTickets.add(TicketPurchasedDTO(purchasedTicket.id, purchasedTicket.issuedAt, purchasedTicket.expiry, purchasedTicket.zoneID,
                createTicketJwt(purchasedTicket.id.toString(), purchasedTicket.issuedAt, purchasedTicket.expiry, purchasedTicket.zoneID)
            ))
        }

        return purchasedTickets
    }

    override fun getTravelers() : List<UserDetailsDTO>{
        val usersList = mutableListOf<UserDetailsDTO>()
        val users = userDetailsRepository.findAll()
        for (u in users!!){
            usersList.add(u.toDTOUserDetails())
        }
        return usersList
    }

    private fun createTicketJwt(sub: String, iat: Timestamp, exp: Timestamp, zid: String) : String {
        val encodedSecret: String = Base64.getEncoder().encodeToString(ticketSecret.toByteArray())
        val jws: String = Jwts.builder() // (1)
            .setSubject(sub) // (2)
            .signWith(SignatureAlgorithm.HS256, encodedSecret) // (3)
            .setIssuedAt(Date(iat.time))
            .setExpiration(Date(exp.time))
            .claim("zid", zid)
            .compact()
        return jws
    }


    // endpoints relative to GET /admin/traveler/{userID}/tickets and GET /admin/traveler/{userID}/profile use the
    // service functions getProfile and getTickets


}