package it.polito.server.repository

import it.polito.server.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface UserRepository: CrudRepository<User, Long> {
    @Query("from User u where u.nickname<=:username")
    fun findByUsername(@Param("username")username : String): User?
}