package it.polito.server.entity

import it.polito.server.dto.ActivationDTO
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "activations")
class Activation(
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User,

    @Column(updatable = false, nullable = false)
    var token : Long,
    @Column(updatable = false, nullable = false)
    var deadline : LocalDateTime,

    ) {

    @Column(updatable = true, nullable = false)
    var counter : Long = 5

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy="uuid2")
    var id : UUID? = null


    fun toDTO(): ActivationDTO {
        return ActivationDTO(id, counter, user, token, deadline)
    }
}


