package it.polito.server.security

import it.polito.server.entity.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import java.security.SecureRandom

@Configuration
class SecurityConfiguration: WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        val passwordEncoder = passwordEncoder()
        auth.inMemoryAuthentication()
            .withUser("user")
            .password(passwordEncoder.encode("pass"))
            .roles("costumer")
            .and()
            .withUser("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("customer", "admin")
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(20, SecureRandom.getInstanceStrong())

    override fun configure(http: HttpSecurity) {
        super.configure(http)
    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
    }
}