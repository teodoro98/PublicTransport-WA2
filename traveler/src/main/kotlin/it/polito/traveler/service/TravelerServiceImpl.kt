package it.polito.traveler.service

import io.github.g0dkar.qrcode.QRCode
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.traveler.controller.UserDetailsNotFoundException
import it.polito.traveler.controller.UserEmpty
import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.dto.UserDetailsLiteDTO
import it.polito.traveler.entity.TicketPurchased
import it.polito.traveler.entity.UserDetails
import it.polito.traveler.repository.TicketPurchasedRepository
import it.polito.traveler.repository.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.sql.Timestamp
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import java.io.ByteArrayOutputStream

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

    @Value("\${server.ticket.expiration.millisec}")
    private lateinit var deltaExpirationTicket: String

    override fun getProfile(username:String): UserDetailsDTO {
        val id = userDetailsRepository.findIdByUsername(username) ?: throw UserDetailsNotFoundException()
        val user = userDetailsRepository.findById(id!!).get()

        return user.toDTOUserDetails()
    }

    override fun createProfile(userDetailsDTO: UserDetailsLiteDTO, username : String) {
        var user = UserDetails(username, userDetailsDTO.address, userDetailsDTO.dateOfBirth, userDetailsDTO.telephoneNumber);
        // var pattern = "^(\\+\\d\\d[\\-\\s]?)?\\d{10}$".toRegex()
        userDetailsRepository.save(user)

    }

    override fun updateProfile(userDetailsDTO: UserDetailsLiteDTO, username : String) {
        var user: UserDetails
        try {
            user = userDetailsRepository.findIdByUsername(username)?.let { userDetailsRepository.findById(it).get() }!!
        }catch (e : Exception){
            throw UserEmpty()
        }
        user.address= userDetailsDTO.address
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
            listTickets.add(TicketPurchasedDTO(t.id, t.issuedAt, t.expiry, t.zoneID, t.type, t.validitytime, t.maxnumberOfRides,
                createTicketJwt(t.id.toString(), t.issuedAt, t.expiry, t.zoneID, t.type, t.validitytime, t.maxnumberOfRides)
            ))
        }
        return listTickets
    }


    override fun getTicket(ticketId: Long): TicketPurchasedDTO {

        val t = ticketPurchasedRepository.findById(ticketId).get();
        val ticket = TicketPurchasedDTO(t.id, t.issuedAt, t.expiry, t.zoneID, t.type, t.validitytime, t.maxnumberOfRides,
            createTicketJwt(t.id.toString(), t.issuedAt, t.expiry, t.zoneID, t.type, t.validitytime, t.maxnumberOfRides)
        )
        return ticket;

    }

    override fun getQr(ticketId: Long, username: String): ByteArrayResource{


        val t = ticketPurchasedRepository.findById(ticketId).get();
        val jws= createqrJwt(username, t.id.toString(), t.issuedAt, t.expiry, t.zoneID, t.type, t.validitytime, t.maxnumberOfRides)

        val imageOut = ByteArrayOutputStream()
        QRCode(jws).render().writeImage(imageOut)

        val imageBytes = imageOut.toByteArray()
        val resource = ByteArrayResource(imageBytes, MediaType.IMAGE_PNG_VALUE)

        return resource;

    }


    override fun buyTickets(result:Boolean , username: String, quantity: Int, zones: String, type:String, validitytime:Timestamp?,  maxnumberOfRides : Int?){

        if (!result) return ;

        val purchasedTickets = mutableListOf<TicketPurchasedDTO>()
        val id: Long = userDetailsRepository.findIdByUsername(username)?: throw UserDetailsNotFoundException()
        val user: UserDetails
        try {
            user = userDetailsRepository.findById(id).get()
        }catch (e : Exception){
            throw UserEmpty()
        }

        val exp: Long = deltaExpirationTicket.toLong() + System.currentTimeMillis()

        repeat(quantity){
            val purchasedTicket = TicketPurchased(
                user,
                java.sql.Timestamp(System.currentTimeMillis()),
                java.sql.Timestamp(exp),
                zones,
                type,
                validitytime,
                maxnumberOfRides
            )
            ticketPurchasedRepository.save(purchasedTicket)
            user.addTicket(purchasedTicket)
            userDetailsRepository.save(user)

            purchasedTickets.add(TicketPurchasedDTO(purchasedTicket.id, purchasedTicket.issuedAt, purchasedTicket.expiry, purchasedTicket.zoneID, purchasedTicket.type, purchasedTicket.validitytime, purchasedTicket.maxnumberOfRides,
                createTicketJwt(purchasedTicket.id.toString(), purchasedTicket.issuedAt, purchasedTicket.expiry, purchasedTicket.zoneID, purchasedTicket.type, purchasedTicket.validitytime, purchasedTicket.maxnumberOfRides)
            ))
        }
    }

    override fun getTravelers() : List<UserDetailsDTO>{
        val usersList = mutableListOf<UserDetailsDTO>()
        val users = userDetailsRepository.findAll()
        for (u in users!!){
            usersList.add(u.toDTOUserDetails())
        }
        return usersList
    }

    private fun createTicketJwt(sub: String, iat: Timestamp, exp: Timestamp, zid: String, type: String, validitytime : Timestamp?, maxnumberOfRides : Int? ) : String {
        val encodedSecret: String = Base64.getEncoder().encodeToString(ticketSecret.toByteArray())
        val jws: String = Jwts.builder() // (1)
            .setSubject(sub) // (2)
            .signWith(SignatureAlgorithm.HS256, encodedSecret) // (3)
            .setIssuedAt(Date(iat.time))
            .setExpiration(Date(exp.time))
            .claim("zid", zid)
            .claim("type", type)
            .claim("validitytime", validitytime)
            .claim("maxnumberOfRides", maxnumberOfRides)
            .compact()
        return jws
    }

    private fun createqrJwt(username:String, sub: String, iat: Timestamp, exp: Timestamp, zid: String, type: String, validitytime : Timestamp?, maxnumberOfRides : Int? ) : String {
        val encodedSecret: String = Base64.getEncoder().encodeToString(ticketSecret.toByteArray())
        val jws: String = Jwts.builder() // (1)
            .setSubject(sub) // (2)
            .signWith(SignatureAlgorithm.HS256, encodedSecret) // (3)
            .setIssuedAt(Date(iat.time))
            .setExpiration(Date(exp.time))
            .claim("zid", zid)
            .claim("type", type)
            .claim("validitytime", validitytime)
            .claim("maxnumberOfRides", maxnumberOfRides)
            .claim("username", username)
            .compact()
        return jws
    }


    // endpoints relative to GET /admin/traveler/{userID}/tickets and GET /admin/traveler/{userID}/profile use the
    // service functions getProfile and getTickets


}