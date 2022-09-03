package it.polito.turnstile.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
/*@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)*/
class WebSecurityConfig {

    private val version: BCryptPasswordEncoder.BCryptVersion = BCryptPasswordEncoder.BCryptVersion.`$2A`
    private val strenght: Int = 12

    @Autowired
	private lateinit var unauthorizedHandler : AuthEntryPointJwt

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(version, strenght)
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  jwtAuthenticationManager: ReactiveAuthenticationManager,
                                  jwtAuthenticationConverter: ServerAuthenticationConverter
    ): SecurityWebFilterChain {

        val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)

        http
            .cors().and().csrf().disable()
            .authorizeExchange()
            .anyExchange().authenticated().and()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)


        return http.build()


        /*.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()*/


    }
}