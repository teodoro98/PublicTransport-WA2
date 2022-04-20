package it.polito.server.entity

import javax.persistence.*

@Entity
@Table(name = "Users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "student_generator")
    @SequenceGenerator(name="student_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1)
    @Column(updatable = false, nullable = false)
    var id : Long? = null


}