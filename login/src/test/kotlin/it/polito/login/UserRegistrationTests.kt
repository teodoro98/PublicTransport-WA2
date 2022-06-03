package it.polito.login



import it.polito.login.controller.EmailNotValid
import it.polito.login.controller.UserEmpty
import it.polito.login.controller.UserNotUnique
import it.polito.login.controller.UserPasswordNotStrong
import it.polito.login.dto.UserDTO
import it.polito.login.entity.Activation
import it.polito.login.entity.User
import it.polito.login.service.UserServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.security.SecureRandom
import java.time.LocalDateTime


@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class UserRegistrationTests {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    private lateinit var userServiceImpl: UserServiceImpl

    @Test
    fun testDTOs() {
        val deadline = LocalDateTime.now()
        val salt = SecureRandom.getInstanceStrong()
        val encoder = BCryptPasswordEncoder(20, salt)
        val user = User(null, "Mario", "mario@gmail.com",encoder.encode("Pwd123456&"), User.Role.COSTUMER)
        val activation = Activation(user, 12345, deadline)
        val userDTO = user.toDTO()
        val activationDTO = activation.toDTO()

        //userDTO Checks
        Assertions.assertEquals(userDTO.id,user.id)
        Assertions.assertEquals(userDTO.email, user.email)
        Assertions.assertEquals(userDTO.nickname, user.nickname)
        Assertions.assertEquals(userDTO.password, user.password)
        Assertions.assertEquals(userDTO.active, user.active)

        //activatiosDTO Checks
        Assertions.assertEquals(activationDTO.id,activation.id)
        Assertions.assertEquals(activationDTO.user,activation.user)
        Assertions.assertEquals(activationDTO.counter,activation.counter)
        Assertions.assertEquals(activationDTO.deadline,activation.deadline)
        Assertions.assertEquals(activationDTO.token,activation.token)

    }

    @Test
    fun constrainsDB() {

        val correctUser      =  UserDTO(null, "Mario", "mario@gmail.com","Pwd123456&", false)
        val sameNameUser     =  UserDTO(null, "Mario", "mario@gmail.com","Pwd123456&", false)
        val emptyNameUser    =  UserDTO(null, "", "mario@gmail.com","Pwd123456&", false)
        val sameEmailUser    =  UserDTO(null, "Carlo", "mario@gmail.com","Pwd123456&", false)
        val emptyEmailUser   =  UserDTO(null, "Mario", "","Pwd123456&", false)
        val invalidEmailUser =  UserDTO(null, "Mario", "mariogmail.com","Pwd123456&", false)
        val invalidPassword1 =  UserDTO(null, "Carlo", "carlo@gmail.com","P1wd&", false)
        val invalidPassword2 =  UserDTO(null, "Carlo", "carlo@gmail.com","PWD123456&", false)
        val invalidPassword3 =  UserDTO(null, "Carlo", "carlo@gmail.com","Pwddsadsadsad&", false)
        val invalidPassword4 =  UserDTO(null, "Carlo", "carlo@gmail.com","Ppwd123456", false)
        val invalidPassword5 =  UserDTO(null, "Carlo", "carlo@gmail.com","ppwd123456&", false)

        //Test correctCase
        Assertions.assertDoesNotThrow {userServiceImpl.registerUser(correctUser)}

        //Tests invalid name
        Assertions.assertThrows(UserNotUnique::class.java)  { userServiceImpl.registerUser(sameNameUser)}
        Assertions.assertThrows(UserEmpty::class.java)      { userServiceImpl.registerUser(emptyNameUser)}

        //Tests invalid email
        Assertions.assertThrows(UserNotUnique::class.java)  { userServiceImpl.registerUser(sameEmailUser)}
        Assertions.assertThrows(UserEmpty::class.java)      { userServiceImpl.registerUser(emptyEmailUser)}
        Assertions.assertThrows(EmailNotValid::class.java)  { userServiceImpl.registerUser(invalidEmailUser)}

        //Tests invalid password
        Assertions.assertThrows(UserPasswordNotStrong::class.java) { userServiceImpl.registerUser(invalidPassword1)}
        Assertions.assertThrows(UserPasswordNotStrong::class.java) { userServiceImpl.registerUser(invalidPassword2)}
        Assertions.assertThrows(UserPasswordNotStrong::class.java) { userServiceImpl.registerUser(invalidPassword3)}
        Assertions.assertThrows(UserPasswordNotStrong::class.java) { userServiceImpl.registerUser(invalidPassword4)}
        Assertions.assertThrows(UserPasswordNotStrong::class.java) { userServiceImpl.registerUser(invalidPassword5)}
        }
    }


