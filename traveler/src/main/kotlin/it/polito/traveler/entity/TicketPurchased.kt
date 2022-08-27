package it.polito.traveler.entity

import it.polito.traveler.dto.TicketPurchasedDTO
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name= "ticketPurchased")
class TicketPurchased (
    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "id")
    var buyer: UserDetails,

    @Column(updatable = false, nullable = false)
    var issuedAt : Timestamp,

    @Column(updatable = false, nullable = false)
    var expiry : Timestamp,

    @Column(updatable = false, nullable = false)
    var zoneID : String
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


}