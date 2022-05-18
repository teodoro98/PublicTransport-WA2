package it.polito.server.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
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
    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Autowired
	private lateinit var unauthorizedHandler : AuthEntryPointJwt

    @Bean
	fun authenticationJwtTokenFilter() : AuthTokenFilter   {
		return AuthTokenFilter();
	}

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        print("bean password encoder chiamato!")
        return BCryptPasswordEncoder(version, strenght)
    }

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers("/users/**").permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}