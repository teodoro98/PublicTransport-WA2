package it.polito.turnstile.repository

import it.polito.turnstile.entity.TurnstileDetatils
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TurnstileDetailsRepository : CoroutineCrudRepository<TurnstileDetatils, Long>
{
    @Transactional
    @Query("select u.id from turnstile_details u where u.username=:username")
    suspend fun findIdByUsername(@Param("username")username : String): Long?
}