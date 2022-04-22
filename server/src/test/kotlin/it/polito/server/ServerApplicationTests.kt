package it.polito.server

import it.polito.server.dto.UserDTO
import it.polito.server.entity.Activation
import it.polito.server.entity.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServerApplicationTests {

    @Test
    fun testDTOs() {
        var user = User(null, "Mario", "mario@gmail.com")
        var activation = Activation(user)
        var userDTO = user.toDTO()
        var activationDTO = activation.toDTO()
        Assertions.assertEquals(userDTO.id,user.id)
        Assertions.assertEquals(userDTO.email, user.email)
        Assertions.assertEquals(userDTO.name, user.name)
        Assertions.assertEquals(activationDTO.id,activation.id)
        Assertions.assertEquals(activationDTO.user,activation.user)
        Assertions.assertEquals(activationDTO.counter,activation.counter)

        }



}
