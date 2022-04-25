package it.polito.server.repository

import it.polito.server.dto.UserDTO
import it.polito.server.entity.Activation
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ActivationRepository:CrudRepository<Activation, UUID?> {
    @Query("from Activation a where a.deadline<=:time")
    fun findByDeadline(@Param("time")time :LocalDateTime): Iterable<Activation>?
    @Query("from Activation a where a.counter<=0")
    fun findByDeadCounter(): Iterable<Activation>?
}