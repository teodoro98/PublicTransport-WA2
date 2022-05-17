package it.polito.traveler.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter? {
        return AuthTokenFilter()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        super.configure(auth)
    }

    override fun configure(http: HttpSecurity) {

        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/test/**").permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter())

        /*
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

            */
    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
    }

}