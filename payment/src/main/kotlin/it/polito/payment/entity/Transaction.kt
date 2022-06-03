package it.polito.payment.entity

import it.polito.payment.dto.TransactionDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "transaction")
class Transaction(

    val userId : Long,
    val orderId: Long,
    val creditCardNumber: String,
    val cardHolder: String
) {

    @Id
    var id : Long? = null

    fun toTransactionDTO() : TransactionDTO{
        return TransactionDTO(id, userId, orderId, creditCardNumber, cardHolder)
    }
}