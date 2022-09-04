package it.polito.turnstile.repository

import it.polito.turnstile.entity.Transit
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface TransitRepository: CoroutineCrudRepository<Transit, Long> {

    @Transactional
    @Query("select from transit t where t.ticket_id=:ticketId order by validation_date")
    suspend fun findFirst(@Param("ticketId")ticketId: Long): Transit?

    @Transactional
    @Query("select count(*) from transit t where t.ticket_id=:ticketId")
    suspend fun countRide(@Param("ticketId")ticketId: Long): Int

    @Transactional
    @Query("select from transit t where t.username=:username and t.validation_date>=:since and t.validation_date<=:to")
    suspend fun findUserTransits(@Param("username")username: String, @Param("since")since: LocalDateTime, @Param("to")to: LocalDateTime): Flow<Transit>
}