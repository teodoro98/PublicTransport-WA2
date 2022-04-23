package it.polito.server.service

import it.polito.server.dto.UserDTO
import org.springframework.stereotype.Service
import org.apache.commons.validator.EmailValidator
import java.util.regex.*
import it.polito.server.controller.*
import it.polito.server.entity.Activation
import it.polito.server.repository.*
import it.polito.server.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Random
import kotlin.math.abs

@Service
class UserServiceImpl: UserService {

    // Repositories
    @Autowired
    private lateinit var userRepository :  UserRepository
    @Autowired
    private lateinit var activationRepository : ActivationRepository

    // Configuration data
    private val deadline = LocalDateTime.now().plusSeconds(30)


    override fun registerUser(user: UserDTO): UserDTO {
        this.validateUser(user)
        try {
            var u = userRepository.save(User(null, user.nickname, user.email, user.password))
            var a = activationRepository.save(Activation(u, abs(Random().nextLong()), deadline))
            u.activation = a
            u = userRepository.save(u)
            return u.toDTO()
        } catch(ex : DataIntegrityViolationException) {
            throw UserNotUnique()
        }
    }

    override fun validateUser(user: UserDTO) {
        if(user.nickname.isEmpty() || user.password.isEmpty() || user.email.isEmpty()) throw UserEmpty()
        if(!EmailValidator.getInstance().isValid(user.email)) throw EmailNotValid()
        if(!this.validatePassword(user.password)) throw UserPasswordNotStrong()
    }

    private fun validatePassword(password: String): Boolean {
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$"
        val p = Pattern.compile(regex);
        val m = p.matcher(password);
        return m.matches();
    }


}