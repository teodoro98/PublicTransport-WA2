package it.polito.turnstile.service

import io.jsonwebtoken.Jwts
import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.entity.Transit
import it.polito.turnstile.repository.TransitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.sql.Time
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

    override suspend fun checkTicket(jwt: String, turnstileUsername: String): Boolean{


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
        val now = LocalDateTime.now();

        if ((zid).contains(this.getTurnstileDetails(turnstileUsername).zoneId) ){

            when(type) {

                "travelcard" -> {
                    //TODO: Not ticketID, ask Teo
                    val firstTimeTicket : Timestamp = Timestamp.valueOf(transitRepository.findFirst(ticketId).date); //call db
                    val effectiveEnd = Timestamp(validitytime.time + firstTimeTicket.time)
                    if (Timestamp.valueOf(now).after(effectiveEnd)) {
                        check=true;
                    }

                }

                "carnet" -> {

                    val numberOfRides = transitRepository.countRide(ticketId)
                    if (maxnumberOfRides>numberOfRides){
                        check=true;
                    }

                }

            }
            if (check){
                //Insert transit
                val transit = Transit(null, turnstileId, ticketId, now)
                transitRepository.save(transit)
            }

        }

        return check
    }

    override suspend fun getTransits(since: LocalDateTime?, to: LocalDateTime?, username: String? ): Flow<TransitDTO> {
        if(since == null && to != null) {
            // Only end
            //TODO
            return transitRepository.findAll().map { it.toDTO() }
        } else if(since != null && to == null){
            // Only beginning
            //TODO
            return transitRepository.findAll().map { it.toDTO() }
        } else if(since != null && to != null){
            // Both beginnig and end
            //TODO
            return if(username != null) {
                transitRepository.findUserTransits(username, since, to).map { t -> t.toDTO() }
            } else {
                //TODO
                transitRepository.findAll().map { it.toDTO() }
            }
        } else {
            // No period time
            //TODO
            return transitRepository.findAll().map { it.toDTO() }
       }
    }

    override suspend fun getSecret(): String{
        return jwtSecret
    }


}