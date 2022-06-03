package it.polito.payment.service

import it.polito.payment.dto.OrderTopic
import it.polito.payment.dto.TransactionDTO
import kotlinx.coroutines.flow.Flow

interface PaymentService {

    suspend fun getTransactions(): Flow<TransactionDTO>

    suspend fun getUserTransactions(userId: Long): Flow<TransactionDTO>

    suspend fun issuePayment(orderTopic: OrderTopic)
}