package it.polito.login.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.login.controller.*
import it.polito.login.dto.*
import it.polito.login.entity.Activation
import it.polito.login.entity.User
import it.polito.login.repository.*
import it.polito.login.security.JwtUtils
import org.apache.commons.validator.EmailValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.regex.*
import kotlin.math.abs


@Service
class UserServiceImpl(@Value("\${server.ticket.token.secret}") clearSecret: String): UserService {

    // Repositories
    @Autowired
    private lateinit var userRepository :  UserRepository
    @Autowired
    private lateinit var activationRepository : ActivationRepository
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Value("\${server.users.activation.enable}")
    private lateinit var activationEnabled: String
    @Value("\${server.users.activation.expire}")
    private lateinit var activationExpire: String

    private val encodedSecret: String = Base64.getEncoder().encodeToString(clearSecret.toByteArray())
    private val algorithm: SignatureAlgorithm = SignatureAlgorithm.HS256

    private var logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun registerUser(user: UserDTO, roles: MutableList<User.Role>): Pair<UserProvDTO?, Long?> {
        this.validateUserData(user)
        val deadline = LocalDateTime.now().plusSeconds(activationExpire.toLong())
        try {
            val encoder = passwordEncoder
            val pwd = encoder.encode(user.password)
            var u = User(null, user.nickname, user.email, pwd, roles)
            if(activationEnabled.toBoolean()) {
                // Users needs to be activated according to DTO
                u.active = user.active
                logger.info("User ${user.nickname} ${if (!u.active) "needs" else "doesn't need"} to be activated")
            } else {
                logger.info("User activation disabled by application settings")
                logger.info("User ${user.nickname} doesn't need to be activated")
                u.active = true
            }
            u = userRepository.save(u)

            if(!u.active) {
                val a = activationRepository.save(Activation(u, abs(Random().nextLong()), deadline))
                u.activation = a
                userRepository.save(u)
                return Pair(UserProvDTO(a.id, u.email), a.token)
            }

            return Pair(null, null)

        } catch(ex : DataIntegrityViolationException) {
            throw UserNotUnique()
        }
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
    override fun pruneExpiredActivation() {
        val time = LocalDateTime.now()
        activationRepository.findByDeadline(time)?.forEach { it ->

            it.user.id?.let { id ->
                val u = userRepository.findById(id).get()
                if(!u.active) userRepository.deleteById(id)
             }
            //activationRepository.deleteById(it.id!!)
        }

        activationRepository.findByDeadCounter()?.forEach { it ->

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

    /*override fun loginUser(user: UserLoginDTO): String {

        val u : User? = userRepository.findByUsername(user.username)

        if (u != null) {
            val encoder = passwordEncoder
            if(encoder.matches(user.password, u.password)) {
                val jwt = createJwt(user.username, u.role)
                return jwt
            } else {
                throw LoginWrongPassword()
            }
        } else {
            throw LoginUserNotFound()
        }
    }*/

    override fun loginUser(user: UserLoginDTO): JwtDTO {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user.username, user.password)
        )
        SecurityContextHolder.getContext().setAuthentication(authentication)
        val jwt: String = jwtUtils.generateJwtToken(authentication)

        return JwtDTO(jwt)
    }

    private fun createJwt(username: String, role: User.Role) : String {
        val builder = Jwts.builder().signWith(algorithm, encodedSecret)
        val iat = java.sql.Date.valueOf(LocalDate.now())
        val exp = java.sql.Date.valueOf(
            iat.toLocalDate().plus(
                1L,
                ChronoUnit.DAYS
            )
        )
        val jwt = builder
            .setSubject(username)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .claim("role", role)
            .compact()

        return jwt

    }


}


@Configuration
@EnableScheduling
@ConditionalOnProperty(name= ["scheduler.enabled"], matchIfMissing = true)
class SchedulerConfig {


}