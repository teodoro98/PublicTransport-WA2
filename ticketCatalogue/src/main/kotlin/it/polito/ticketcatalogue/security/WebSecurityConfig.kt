package it.polito.ticketcatalogue.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)
class WebSecurityConfig {

    private val version: BCryptPasswordEncoder.BCryptVersion = BCryptPasswordEncoder.BCryptVersion.`$2A`
    private val strenght: Int = 12

    @Autowired
	private lateinit var unauthorizedHandler : AuthEntryPointJwt

    @Bean
	fun authenticationJwtTokenFilter() : AuthTokenFilter   {
		return AuthTokenFilter();
	}

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(version, strenght)
    }

    @Bean
    fun chain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.cors().and().csrf().disable()

            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            //.antMatchers("/admin/**").access("hasRole('ADMIN')")
            .anyRequest().authenticated()

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build();

    }
}