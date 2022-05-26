package it.polito.ticketcatalogue.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


class AuthTokenFilter: WebFilter {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    override fun filter(
        serverWebExchange: ServerWebExchange,
        webFilterChain: WebFilterChain
    ): Mono<Void> {

        try {
            val jwt = parseJwt(serverWebExchange.request)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                val userDetails: UserDetails = jwtUtils.getUserFromJwtToken(jwt)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()

                )
                println(authentication)
                /*val req = serverWebExchange.request
                authentication.details = WebAuthenticationDetailsSource().buildDetails(serverWebExchange.request as javax.servlet.http.HttpServletRequest)*/
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error("Cannot set user authentication: {}", e)
        }

        return webFilterChain.filter(serverWebExchange)
    }

    private fun parseJwt(request: org.springframework.http.server.reactive.ServerHttpRequest): String? {
        val headerAuth: String? = request.headers["Authorization"]?.get(0)
        if (headerAuth != null) {
            if (headerAuth.isNotEmpty() && headerAuth.startsWith("Bearer ")) {
                return headerAuth.substring(7, headerAuth.length)
            }
        }
        return null
    }
}