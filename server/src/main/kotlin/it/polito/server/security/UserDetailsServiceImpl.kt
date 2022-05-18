package it.polito.server.security

import it.polito.server.entity.User
import it.polito.server.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserDetailsServiceImpl: UserDetailsService {
    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val userDetails : UserDetails
        val user : User = userRepository.findByUsername(username!!)
            ?: throw UsernameNotFoundException("User $username not found")

        userDetails = UserDetailsImpl.build(user)
        return userDetails
    }
}