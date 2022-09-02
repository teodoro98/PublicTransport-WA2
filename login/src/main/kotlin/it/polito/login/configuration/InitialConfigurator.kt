package it.polito.login.configuration

import it.polito.login.dto.UserDTO
import it.polito.login.entity.User
import it.polito.login.repository.UserRepository
import it.polito.login.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class InitialConfigurator {

    @Value("\${server.users.default.admin.nickname}")
    private lateinit var defaultAdminNickname: String
    @Value("\${server.users.default.admin.password}")
    private lateinit var defaultAdminPassword: String
    @Value("\${server.users.default.admin.email}")
    private lateinit var defaultAdminEmail: String

    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var repository: UserRepository

    private var logger: Logger = LoggerFactory.getLogger(InitialConfigurator::class.java)

    @PostConstruct
    fun setupDefaultAdmin() {
        logger.info("Checking default admin user presence...")
        if(repository.findAll().count { u -> u.role.contains(User.Role.ROLE_RECRUITER) } == 0) {
            //No admin recruiter is present

            val u = UserDTO(
                null,
                defaultAdminNickname,
                defaultAdminEmail,
                defaultAdminPassword,
                true
            )

            userService.registerUser(u, mutableListOf(User.Role.ROLE_ADMIN, User.Role.ROLE_RECRUITER))

            logger.info("Checking default admin user presence...")
        } else {
            println("Admin user already present")
        }
    }

}