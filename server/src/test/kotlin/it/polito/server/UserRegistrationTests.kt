package it.polito.server



import it.polito.server.controller.EmailNotValid
import it.polito.server.controller.UserEmpty
import it.polito.server.controller.UserNotUnique
import it.polito.server.controller.UserPasswordNotStrong
import it.polito.server.entity.Activation
import it.polito.server.entity.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDateTime



@SpringBootTest
class UserRegistrationTests {

    @Autowired
    private lateinit var userServiceImpl: UserServiceImpl

    @Test
    fun testDTOs() {
        var deadline = LocalDateTime.now()
        var user = User(null, "Mario", "mario@gmail.com","Pwd123456&")
        var activation = Activation(user, 12345, deadline)
        var userDTO = user.toDTO()
        var activationDTO = activation.toDTO()

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
    fun prova(userRepository: UserRepository){
        var correctUser = User(null, "Mario", "mario@gmail.com","Pwd123456&")
        Assertions.assertDoesNotThrow{userRepository.save(correctUser)}
    }

    @Test
    fun constrainsDB() {

        val correctUser      =  User(null, "Mario", "mario@gmail.com","Pwd123456&").toDTO()
        val sameNameUser     =  User(null, "Mario", "mario@gmail.com","Pwd123456&").toDTO()
        val emptyNameUser    =  User(null, "", "mario@gmail.com","Pwd123456&").toDTO()
        val sameEmailUser    =  User(null, "Carlo", "mario@gmail.com","Pwd123456&").toDTO()
        val emptyEmailUser   =  User(null, "Mario", "","Pwd123456&").toDTO()
        val invalidEmailUser =  User(null, "Mario", "mariogmail.com","Pwd123456&").toDTO()
        val invalidPassword1 =  User(null, "Carlo", "carlo@gmail.com","P1wd&").toDTO()
        val invalidPassword2 =  User(null, "Carlo", "carlo@gmail.com","PWD123456&").toDTO()
        val invalidPassword3 =  User(null, "Carlo", "carlo@gmail.com","Pwddsadsadsad&").toDTO()
        val invalidPassword4 =  User(null, "Carlo", "carlo@gmail.com","Ppwd123456").toDTO()
        val invalidPassword5 =  User(null, "Carlo", "carlo@gmail.com","ppwd123456&").toDTO()

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

}
