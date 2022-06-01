package it.polito.payment.dto

data class TransactionDTO (
    val id : Long? = null,
    val userId: Long,
    val orderId: Long
)