package it.polito.server.entity

import javax.persistence.*


@Entity
@Table(name = "users")
class User (
    @OneToOne(mappedBy = "user", cascade = arrayOf(CascadeType.ALL))
    var activation: Activation?
   ) {
    var name : String = ""
    var email: String = ""
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