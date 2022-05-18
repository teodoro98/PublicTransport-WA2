package it.polito.traveler.security


import org.springframework.security.core.GrantedAuthority
import java.util.stream.Collectors





class UserDetailsImpl(private val id: Long, private val username : String, private val email: String, private val password: String,
                      private val authorities: Collection<GrantedAuthority?> ): org.springframework.security.core.userdetails.UserDetails {

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authorities
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun build(user: User): UserDetailsImpl? {
        val authorities: List<GrantedAuthority?> = user.getRoles().stream()
            .map { role -> SimpleGrantedAuthority(role.getName().name()) }
            .collect(Collectors.toList())
        return UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            authorities
        )
    }
}