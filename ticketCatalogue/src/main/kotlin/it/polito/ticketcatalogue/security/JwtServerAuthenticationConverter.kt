package it.polito.ticketcatalogue.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtServerAuthenticationConverter : ServerAuthenticationConverter {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {
        return Mono.justOrEmpty(exchange)
            .flatMap { Mono.justOrEmpty(parseJwt(it)) }
            .filter { it.isNotEmpty() }
            .map {
                UsernamePasswordAuthenticationToken(
                    it, it
                )
            }
    }

    private fun parseJwt(exchange: ServerWebExchange): String? {
        val headerAuth: String? = exchange.request.headers["Authorization"]?.get(0)
        if (headerAuth != null) {
            if (headerAuth.isNotEmpty() && headerAuth.startsWith("Bearer ")) {
                return headerAuth.substring(7, headerAuth.length)
            }
        }
        return null
    }
}