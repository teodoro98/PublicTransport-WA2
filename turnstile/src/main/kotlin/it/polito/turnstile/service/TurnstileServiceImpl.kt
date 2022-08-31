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

@Service
class TurnstileServiceImpl(): TurnstileService  {

    @Value("\${server.login.token.secret}")
    private var jwtSecret: String = ""

    @Autowired
    private lateinit var transitRepository :  TransitRepository

    override suspend fun checkTicket(jwt: String): Boolean{


        val ticketId = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["id"] as Long
        val iatString = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["iat"]
        val iat = Timestamp.valueOf(iatString as String);

        val expString  = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["exp"]
        val exp = Timestamp.valueOf(expString as String);


        val zid  = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["zid"] as String

        val type = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["type"] as String
        //TYPE ticket, travelcard, carnet

        val validitytimeString = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["validitytime"]
        val validitytime = Timestamp.valueOf(validitytimeString as String);


        val maxnumberOfRides = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["maxtransit"] as Int


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

        val turnstileId = 0L
        val turnstileZid = "A";


        var check = false
        val now = LocalDateTime.now();

        if (Timestamp.valueOf(now).after(exp) && (zid).contains(turnstileZid) ){

            when(type) {

                "travelcard" -> {

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

    override suspend fun getTransits(): Flow<TransitDTO> {
        return transitRepository.findAll().map { it.toDTO() }
    }

    override suspend fun getSecret(): String{
        return jwtSecret
    }


}