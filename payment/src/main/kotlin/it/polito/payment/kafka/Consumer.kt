package it.polito.payment.kafka

import it.polito.payment.dto.OrderTopic
import it.polito.payment.service.PaymentService
import it.polito.payment.service.PaymentServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class Consumer {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var paymentService: PaymentServiceImpl

    @KafkaListener(topics = ["\${kafka.topics.product}"], groupId = "ppr")
    fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received {}", consumerRecord)
        ack.acknowledge()

        val order = consumerRecord.value() as OrderTopic
        val scope = CoroutineScope(Job())
        scope.launch {
            paymentService.issuePayment(order)
        }

    }
}
