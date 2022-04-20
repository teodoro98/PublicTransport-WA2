package it.polito.server.entity

import org.hibernate.annotations.GenericGenerator
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
    var user: User
    ) {
    var counter : Long = 5

    @Id
    @Column(columnDefinition = "CHAR(32)",updatable = false, nullable = false)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy="uuid2")
    var id : UUID? = null

}