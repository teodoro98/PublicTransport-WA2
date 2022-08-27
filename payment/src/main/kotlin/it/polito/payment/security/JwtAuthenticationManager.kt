package it.polito.payment.security

import io.jsonwebtoken.impl.crypto.JwtSigner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager() : ReactiveAuthenticationManager {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .filter { jwtUtils.validateJwtToken(it.credentials as String) }
            .map { jwtUtils.getUserFromJwtToken(it.credentials as String) }
            .map { UsernamePasswordAuthenticationToken(it, null, it.authorities) }
    }
}