package it.polito.turnstile.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors

@Component
class JwtUtils {
    @Value("\${server.login.token.secret}")
    private var jwtSecret: String = ""

    @Value("\${server.ticket.token.expirationms}")
    private var jwtExpirationMs: Int = 0

    fun generateJwtToken(userPrincipal: UserDetailsImpl): String {
        val roles: List<String> = userPrincipal.authorities.stream()
            .map { item -> item!!.authority }
            .collect(Collectors.toList())
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .claim("roles", roles.toString())
            .claim("id", userPrincipal.getId())
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, jwtSecret)
            .compact()
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject()
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: io.jsonwebtoken.SignatureException) {
            logger.error("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }

    fun getUserFromJwtToken(jwt: String): UserDetailsImpl {
        val username: String = getUserNameFromJwtToken(jwt)
        val rolesParsed = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["roles"]
        val roles = mutableListOf<GrantedAuthority>()
        val listString: String = rolesParsed.toString().substring(1, rolesParsed.toString().length - 1)
        val stringToken = StringTokenizer(listString, ",")
        for (token in stringToken) {
            if(token is String) {
                roles.add(SimpleGrantedAuthority((token).trim()))
            }
        }

        val id = (Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).body["id"] as Int).toLong()
        return UserDetailsImpl(id, username, "", roles)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtUtils::class.java)
    }
}