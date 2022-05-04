package it.polito.server.entity

import it.polito.server.dto.UserDTO
import it.polito.server.dto.UserSlimDTO
<<<<<<< HEAD
import it.polito.server.dto.UserLoginDTO
=======
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
>>>>>>> 1a79aa4f31f4a304548d1851cc3a52044ff47d48
import javax.persistence.*


@Entity
@Table(name = "users")
class User (

    @OneToOne(mappedBy = "user", cascade = arrayOf(CascadeType.ALL))
    var activation: Activation?,

    @Column(updatable = true, nullable = false, unique = true)
    var nickname: String,

    @Column(updatable = true, nullable = false, unique = true)
    var email: String,

    @Column(updatable = true, nullable = false, )
    var password: String

   ) {

    @Column(updatable = true, nullable = false, )
    var role : Role = Role.COSTUMER

    @Column(updatable = true, nullable = false, )
    var active : Boolean = false

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "user_generator")
    @SequenceGenerator(name="user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1)
    @Column(updatable = false, nullable = false)
    var id : Long? = null

    init {
        password = BCryptPasswordEncoder().encode(password)
    }

    fun toDTO(): UserDTO {
        return UserDTO(id, nickname, email, password, active, role)
    }
    

    fun toDTOSlim(): UserSlimDTO {
        return UserSlimDTO(id, nickname, email)
    }


    fun toDTOLogin(): UserLoginDTO {
        return UserLoginDTO(nickname, password)
    }
}

