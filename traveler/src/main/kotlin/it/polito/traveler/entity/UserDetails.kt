package it.polito.traveler.entity

import it.polito.traveler.dto.UserDetailsDTO
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name= "userDetails")
class UserDetails (
    @Column(updatable = true, nullable = false, )
    var name: String,
    @Column(updatable = true, nullable = false, )
    var address: String,
    @Column(updatable = true, nullable = false, )
    var dateOfBirth: LocalDate,
    @Column(updatable = true, nullable = false, )
    var telephoneNumber: Number,
        ) {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "user_generator")
    @SequenceGenerator(name="user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1)
    @Column(updatable = false, nullable = false)
    var id : Long? = null

    @OneToMany(mappedBy = "ticketPurchased")
    val ticketPurchased = mutableSetOf<TicketPurchased>()

    fun addTicket(t: TicketPurchased) {
        ticketPurchased.add(t)
    }

    fun toDTOUserDetails(): UserDetailsDTO{
        return UserDetailsDTO(id,name,address,dateOfBirth, telephoneNumber)
    }

    enum class Role {
        COSTUMER, ADMIN
    }
}