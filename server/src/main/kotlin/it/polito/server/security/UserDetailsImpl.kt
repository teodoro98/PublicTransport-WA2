package it.polito.server.security


import it.polito.server.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.stream.Collectors

class UserDetailsImpl(private val id: Long, private val username : String, private val email: String, private val password: String,
                      private val authorities: Collection<GrantedAuthority?> ): org.springframework.security.core.userdetails.UserDetails {

    fun getId(): Long{
        return id
    }

    fun getEmail(): String{
        return email
    }

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

    companion object {
        fun build(user: User): UserDetailsImpl {
            val authorities: List<GrantedAuthority?> = listOf(SimpleGrantedAuthority(user.role.name))
            return UserDetailsImpl(
                user.id!!,
                user.nickname,
                user.email,
                user.password,
                authorities
            )
        }
    }

}