package it.polito.payment.entity

import it.polito.payment.dto.TransactionDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "transaction")
class Transaction(
    @Id
    val id : Long? = null,
    val userId : Long,
    val orderId: Long
) {
    fun toTransactionDTO() : TransactionDTO{
        return TransactionDTO(id, userId, orderId)
    }
}