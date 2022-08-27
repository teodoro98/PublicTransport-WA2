package it.polito.traveler.entity

import it.polito.traveler.dto.UserDetailsDTO
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name= "userDetails")
class UserDetails (
    @Column(updatable = true, nullable = false, )
    var username: String,
    @Column(updatable = true, nullable = false, )
    var address: String,
    @Column(updatable = true, nullable = false, )
    var dateOfBirth: LocalDate,
    @Column(updatable = true, nullable = false, )
    var telephoneNumber: Long,
        ) {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "userdetail_generator")
    @SequenceGenerator(name="userdetail_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1)
    @Column(updatable = false, nullable = false)
    var id : Long? = null

    @OneToMany(mappedBy = "buyer")
    val ticketPurchased = mutableSetOf<TicketPurchased>()

    fun addTicket(t: TicketPurchased) {
        ticketPurchased.add(t)
    }

    fun toDTOUserDetails(): UserDetailsDTO{
        return UserDetailsDTO(id,username,address,dateOfBirth, telephoneNumber)
    }


}