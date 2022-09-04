package it.polito.turnstile.service

import io.jsonwebtoken.Jwts
import it.polito.turnstile.controller.*
import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.dto.TurnstileDetailsDTO
import it.polito.turnstile.entity.Transit
import it.polito.turnstile.entity.TurnstileDetatils
import it.polito.turnstile.repository.TransitRepository
import it.polito.turnstile.repository.TurnstileDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
        val zid  = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["zid"] as String
        val type = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["type"] as String
        //TYPE travelcard, carnet

        val now = LocalDateTime.now()

        if ((zid).contains(this.getTurnstileDetails(turnstileUsername).zoneId) ){

            when(type) {
                "travelcard" -> {
                    val validitytimeString = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["validitytime"] as Long
                    val validitytime = Timestamp(validitytimeString)
                    val firstTransit = transitRepository.findFirst(ticketId)
                    if(firstTransit != null) {
                        // A first transit exists
                        val firstTimeTicket : Timestamp = Timestamp.valueOf(firstTransit.validation_date)
                        // Get effective end of the validity period of time starting from FIRST transit
                        val effectiveEnd = Timestamp(validitytime.time + firstTimeTicket.time)
                        if (Timestamp.valueOf(now).before(effectiveEnd)) {
                            // The end is after today
                            throw ValidityTimeExpiredException()
                        }
                    }
                }
                "carnet" -> {
                    val maxnumberOfRides = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["maxnumberOfRides"] as Int
                    val numberOfRides = transitRepository.countRide(ticketId)
                    if (maxnumberOfRides<=numberOfRides){
                        // Reached max number of rides
                        throw MaxRidesReachedException()
                    }
                }
                else -> {
                    // Type not found
                    throw TypeNotFoundException()
                }
            }
            //Insert transit
            val transit = Transit(null, ticketId, user, turnstileUsername, now)
            transitRepository.save(transit)
        }
    }

    override suspend fun getTransits(since: LocalDateTime?, to: LocalDateTime?, username: String? ): Flow<TransitDTO> {
        if(since == null && to != null) {
            // Only end
            return if(username != null) {
                val transits = transitRepository.findUserTransitsTo(username, to)
                transits.map { t -> t.toDTO() }
            } else {
                transitRepository.findCompanyTransitsTo(to).map { it.toDTO() }
            }
        } else if(since != null && to == null){
            // Only beginning
            return if(username != null) {
                val transits = transitRepository.findUserTransitsSince(username, since)
                transits.map { t -> t.toDTO() }
            } else {
                transitRepository.findCompanyTransitsSince(since).map { it.toDTO() }
            }
        } else if(since != null && to != null){
            // Both beginnig and end
            return if(username != null) {
                val transits = transitRepository.findUserTransits(username, since, to)
                transits.map { t -> t.toDTO() }
            } else {
                transitRepository.findCompanyTransits(since, to).map { it.toDTO() }
            }

        } else {
            // No period time
            return if(username != null) {
                val transits = transitRepository.findUserTransits(username)
                transits.map { t -> t.toDTO() }
            } else {
                transitRepository.findCompanyTransits().map { it.toDTO() }
            }
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

            throw TurnstileNotFoundException()
        }

    }


}