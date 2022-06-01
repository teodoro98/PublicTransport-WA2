package it.polito.payment.service

import it.polito.payment.dto.TransactionDTO
import it.polito.payment.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PaymentServiceImpl: PaymentService {
    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    override suspend fun getTransactions(): Flow<TransactionDTO> {
        return transactionRepository.findAll().map { it.toTransactionDTO() }
    }

    override suspend fun getUserTransactions(userId: Long): Flow<TransactionDTO> {
        return transactionRepository.findByUserId(userId).map { it.toTransactionDTO() }
    }
}