package it.polito.server.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import org.apache.commons.validator.EmailValidator
import java.util.regex.*
import it.polito.server.controller.*
import it.polito.server.dto.*
import it.polito.server.entity.Activation
import it.polito.server.repository.*
import it.polito.server.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDateTime
import kotlin.math.abs
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.SecureRandom
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*


@Service
class UserServiceImpl(@Value("\${server.ticket.token.secret}") clearSecret: String): UserService {

    // Repositories
    @Autowired
    private lateinit var userRepository :  UserRepository
    @Autowired
    private lateinit var activationRepository : ActivationRepository

    private val encodedSecret: String = Base64.getEncoder().encodeToString(clearSecret.toByteArray())
    private val algorithm: SignatureAlgorithm = SignatureAlgorithm.HS256
    private val version: BCryptPasswordEncoder.BCryptVersion = BCryptPasswordEncoder.BCryptVersion.`$2A`
    private val strenght: Int = 20


    override fun registerUser(user: UserDTO): Pair<UserProvDTO, Long> {
        this.validateUserData(user)
        val deadline = LocalDateTime.now().plusSeconds(20)
        try {
            val salt = SecureRandom.getInstanceStrong()
            val encoder = BCryptPasswordEncoder(version, strenght, salt)
            val u = userRepository.save(User(null, user.nickname, user.email, encoder.encode(user.password), User.Role.COSTUMER, salt))
            val a = activationRepository.save(Activation(u, abs(Random().nextLong()), deadline))
            u.activation = a
            userRepository.save(u)
            return Pair(UserProvDTO(a.id, u.email), a.token)
        } catch(ex : DataIntegrityViolationException) {
            throw UserNotUnique()
        }
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
    override fun pruneExpiredActivation() {
        val time = LocalDateTime.now()
        activationRepository.findByDeadline(time)?.forEach { it ->
            println("Cancellato per deadline" + it.toString() + "DELETED")
            it.user.id?.let { id -> userRepository.deleteById(id) }
            //activationRepository.deleteById(it.id!!)
        }

        activationRepository.findByDeadCounter()?.forEach { it ->
            println("Cancellato per counter" +it.toString() + "DELETED")
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
        if(LocalDateTime.now().isAfter(act.deadline)) {
            throw ActivationCodeExpired()
        }
        if(act.counter == 0L) {
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

    override fun loginUser(user: UserLoginDTO): String {

        val u : User? = userRepository.findByUsername(user.username)

        if (u != null) {
            if(BCryptPasswordEncoder().matches(user.password, u.password)) {
                return createJwt(user.username, u.role)
            } else {
                //TODO("Wrong password exception and handler")
                throw Exception()
            }
        } else {
            //TODO("Not found user exception and handler")
            throw Exception()
        }
    }

    private fun createJwt(username: String, role: User.Role) : String {
        val builder = Jwts.builder().signWith(algorithm, encodedSecret)
        val iat = java.sql.Date.valueOf(LocalDate.now())
        val exp = java.sql.Date.valueOf(
            iat.toLocalDate().plus(
                1L,
                ChronoUnit.HOURS
            )
        )
        return builder
            .setSubject(username)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .claim("role", role)
            .compact()
    }


}


@Configuration
@EnableScheduling
@ConditionalOnProperty(name= ["scheduler.enabled"], matchIfMissing = true)
class SchedulerConfig {


}