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
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        super.configure(auth)
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/api/admin/travelers").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/admin/traveler/${userID}/profile").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/api/admin/traveler/${userID}/tickets").hasRole("ADMIN")
            .anyRequest()
            .authenticated()
            /*.and()
            .oauth2ResourceServer()
            .jwt()
            .decoder(jwtDecoder());*/
    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
    }

}