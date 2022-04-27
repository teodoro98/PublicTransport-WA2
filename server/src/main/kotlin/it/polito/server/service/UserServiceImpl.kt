package it.polito.server.service

import org.springframework.stereotype.Service
import org.apache.commons.validator.EmailValidator
import java.util.regex.*
import it.polito.server.controller.*
import it.polito.server.dto.*
import it.polito.server.entity.Activation
import it.polito.server.repository.*
import it.polito.server.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDateTime
import java.util.Random
import kotlin.math.abs
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled


@Service
class UserServiceImpl: UserService {

    // Repositories
    @Autowired
    private lateinit var userRepository :  UserRepository
    @Autowired
    private lateinit var activationRepository : ActivationRepository

    // Configuration data
    private val deadline = LocalDateTime.now().plusSeconds(2)


    override fun registerUser(user: UserDTO): UserProvDTO {
        this.validateUserData(user)
        try {
            val u = userRepository.save(User(null, user.nickname, user.email, user.password))
            val a = activationRepository.save(Activation(u, abs(Random().nextLong()), deadline))
            u.activation = a
            userRepository.save(u)
            return UserProvDTO(a.id, u.email)
        } catch(ex : DataIntegrityViolationException) {
            throw UserNotUnique()
        }
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
    override fun pruneExpiredActivation() {
        val time = LocalDateTime.now()
        activationRepository.findByDeadline(time)?.forEach { it ->
            println(it.toString() + "DELETED")
            it.user.id?.let { id -> userRepository.deleteById(id) }
            //activationRepository.deleteById(it.id!!)
        }

        activationRepository.findByDeadCounter()?.forEach { it ->
            println(it.toString() + "DELETED")
            it.user.id?.let { id -> userRepository.deleteById(id) }
            //activationRepository.deleteById(it.id!!)
        }

    }

    override fun validateUserData(user: UserDTO) {
        if(user.nickname.isEmpty() || user.password.isEmpty() || user.email.isEmpty()) throw UserEmpty()
        if(!EmailValidator.getInstance().isValid(user.email)) throw EmailNotValid()
        if(!this.validatePassword(user.password)) throw UserPasswordNotStrong()
    }

    override fun validateUserEmail(validation: ValidationDTO): UserSlimDTO {
        val act  = activationRepository.findById(validation.provisional_id).orElseThrow {
            throw ActivationIDNotFound()
        }
        if(LocalDateTime.now() >= act.deadline) {
            throw ActivationCodeExpired()
        }
        if(act.counter > 0) {
            throw ActivationCodeExpired()
        }
        if(act.token != validation.activation_code) {
            act.counter--
            activationRepository.save(act)
            throw ActivationCodeMismatch()
        }
        act.user.active = true
        val u = userRepository.save(act.user)
        return u.toDTOSlim()
    }

    private fun validatePassword(password: String): Boolean {
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$"
        val p = Pattern.compile(regex)
        val m = p.matcher(password)
        return m.matches()
    }


}


@Configuration
@EnableScheduling
@ConditionalOnProperty(name= ["scheduler.enabled"], matchIfMissing = true)
class SchedulerConfig {


}