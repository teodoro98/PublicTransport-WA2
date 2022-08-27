package it.polito.payment.service

import it.polito.payment.controller.InternalServerErrorException
import it.polito.payment.dto.OrderTopic
import it.polito.payment.dto.ResultDTO
import it.polito.payment.dto.TransactionDTO
import it.polito.payment.dto.UserOrder
import it.polito.payment.entity.Transaction
import it.polito.payment.repository.TransactionRepository
import it.polito.payment.security.UserDetailsImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.apache.kafka.common.requests.DeleteAclsResponse.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate


@Service
class PaymentServiceImpl(
    @Value("\${kafka.topics.result}") val topic: String,
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>
): PaymentService {
    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    override suspend fun getTransactions(): Flow<TransactionDTO> {
        return transactionRepository.findAll().map { it.toTransactionDTO() }
    }

    override suspend fun getUserTransactions(userId: Long): Flow<TransactionDTO> {
        return transactionRepository.findByUserId(userId).map { it.toTransactionDTO() }
    }

    override suspend fun issuePayment(orderTopic: OrderTopic) {
        var res: Boolean = true

        val exp: String= orderTopic.paymentInfo.exp
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = LocalDate.parse(exp, formatter)
        if(date.isBefore(LocalDate.now())) res = false

        delay(5000)
        if(res){
            val transaction =Transaction(   orderTopic.userDetails.id,
                                            orderTopic.orderId,
                                            orderTopic.paymentInfo.creditCardNumber,
                                            orderTopic.paymentInfo.cardHolder)
            transactionRepository.save(transaction)
        }


        sendPaymentResult(orderTopic.userDetails, orderTopic.orderId, res)
    }

    private suspend fun sendPaymentResult(userDetails: UserOrder, orderId: Long, result: Boolean) {
        val resultDTO = ResultDTO(userDetails, orderId, result)
        try {
            log.info("Receiving payment result")
            log.info("Sending message to Kafka {}", resultDTO)
            val message: Message<ResultDTO> = MessageBuilder
                .withPayload(resultDTO)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
            log.info("Message sent with success")
        } catch (e: Exception) {
            log.error("Exception: {}", e)
            throw InternalServerErrorException()
        }
    }
}