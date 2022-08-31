package it.polito.turnstile.repository

import it.polito.turnstile.entity.Transit
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TransitRepository: CoroutineCrudRepository<Transit, Long> {

    @Transactional
    @Query("select from transit t where t.ticketId=:ticketId order by date asc")
    suspend fun findFirst(@Param("ticketId")ticketId: Long): Transit

    @Transactional
    @Query("select count(*) from transit t where t.ticketId=:ticketId")
    suspend fun countRide(@Param("ticketId")ticketId: Long): Int
}