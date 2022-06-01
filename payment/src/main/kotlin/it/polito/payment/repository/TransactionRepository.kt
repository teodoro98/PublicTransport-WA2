package it.polito.payment.repository

import it.polito.payment.entity.Transaction
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

@Repository
interface TransactionRepository: CoroutineCrudRepository<Transaction, Long> {
    @Transactional
    @Query("select * from transaction t where t.userId=:buyerId")
    fun findByUserId(@Param("userID")userID : Long): Flow<Transaction>
}