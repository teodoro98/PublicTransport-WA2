package it.polito.turnstile.service

import io.jsonwebtoken.Jwts
import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.dto.TurnstileDetailsDTO
import it.polito.turnstile.entity.Transit
import it.polito.turnstile.entity.TurnstileDetatils
import it.polito.turnstile.repository.TransitRepository
import it.polito.turnstile.repository.TurnstileDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Service
class TurnstileServiceImpl(@Value("\${server.ticket.token.secret}") clearSecret: String): TurnstileService  {

    private val jwtSecret: String = Base64.getEncoder().encodeToString(clearSecret.toByteArray())

    @Autowired
    private lateinit var transitRepository :  TransitRepository

    @Autowired
    private lateinit var turnstileDetailsRepository :  TurnstileDetailsRepository

    override suspend fun checkTicket(jwt: String, turnstileUsername: String){


        val ticketId = (Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["sub"] as String).toLong()
        val user = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["username"] as String
        /*val iatString = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["iat"]
        val iat = Timestamp((iatString as String).toLong())

        val expString  = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["exp"]
        val exp = Timestamp.valueOf(expString as String)
        */

        val zid  = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["zid"] as String

        val type = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["type"] as String
        //TYPE travelcard, carnet

        val validitytimeString = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["validitytime"] as Long
        val validitytime = Timestamp(validitytimeString)


        val maxnumberOfRides = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["maxnumberOfRides"] as Int


        /*

        val turnstile = UserDetailsImplem ...
        val turnstileId = 0L
        val turnstileZid = "A";
        */


/*
        val ticketId = 24L;
        val iat : Timestamp = Timestamp.valueOf("1661606598");       //timestamp
        val exp : Timestamp = Timestamp.valueOf("1661873245");       //timestamp
        val zid = "ADB";

        val type = "carnet";
        val validitytime : Timestamp = Timestamp.valueOf("1661606598");
        val maxnumberOfRides = 10;
*/



        var check = false
        val now = LocalDateTime.now()

        if ((zid).contains(this.getTurnstileDetails(turnstileUsername).zoneId) ){

            when(type) {
                "travelcard" -> {
                    //TODO: Not ticketID, ask Teo
                    val firstTransit = transitRepository.findFirst(ticketId)
                    if(firstTransit != null) {
                        // A first transit exists
                        val firstTimeTicket : Timestamp = Timestamp.valueOf(firstTransit.validation_date)
                        // Get effective end of the validity period of time starting from FIRST transit
                        val effectiveEnd = Timestamp(validitytime.time + firstTimeTicket.time)
                        if (Timestamp.valueOf(now).after(effectiveEnd)) {
                            // The end is after today
                            check=true
                        }
                    } else {
                        check = true
                    }
                }
                "carnet" -> {
                    val numberOfRides = transitRepository.countRide(ticketId)
                    if (maxnumberOfRides>numberOfRides){
                        check=true
                    }
                }
                else -> {
                    // Type not found
                    // TODO TypeNotFoundException
                    throw Exception("Type not found exception")
                }
            }
            if (check){
                //Insert transit
                val transit = Transit(null, ticketId, user, turnstileUsername, now)
                transitRepository.save(transit)
            }
        }
        // TODO TicketNotValidException
        if(!check) throw Exception("Ticket not valid")
    }

    override suspend fun getTransits(since: LocalDateTime?, to: LocalDateTime?, username: String? ): Flow<TransitDTO> {
        if(since == null && to != null) {
            // Only end
            //TODO Query find only end for single username
            //TODO Query find only end FOR ALL USERNAMES
            return transitRepository.findAll().map { it.toDTO() }
        } else if(since != null && to == null){
            // Only beginning
            //TODO Query find only beginning for single username
            //TODO Query find only beginning FOR ALL USERNAMES
            return transitRepository.findAll().map { it.toDTO() }
        } else if(since != null && to != null){
            // Both beginnig and end
            return if(username != null) {
                transitRepository.findUserTransits(username, since, to).map { t -> t.toDTO() }
            } else {
                //TODO Query find in period time FOR ALL USERNAMES
                transitRepository.findAll().map { it.toDTO() }
            }
        } else {
            // No period time
            //TODO Query get all for single username
            //TODO Query get all FOR ALL USERNAMES
            return transitRepository.findAll().map { it.toDTO() }
       }
    }

    override suspend fun getSecret(): String{
        return jwtSecret
    }

    override suspend fun addDetails(detailsDTO: TurnstileDetailsDTO) {

        val turnstileDetails = TurnstileDetatils(null, detailsDTO.username, detailsDTO.zoneId )
        turnstileDetailsRepository.save(turnstileDetails)

    }

    override suspend fun getTurnstileDetails(turnstileUsername : String) : TurnstileDetailsDTO {

        val turnstileId= turnstileDetailsRepository.findIdByUsername(turnstileUsername)

        if(turnstileId != null) {
            val turnstileDetails = turnstileDetailsRepository.findById(turnstileId)?.toDTO()

            return turnstileDetails!!
        }else {
            //TODO: TurnstileNotFound exception
            throw Exception("TurnstileNotFound")
        }

    }


}