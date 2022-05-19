package it.polito.traveler.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

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

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers("/my/**").permitAll()
            .antMatchers("/admin/**").permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}