package it.polito.ticketcatalogue.kafka

import it.polito.ticketcatalogue.dto.ResultDTO
import it.polito.ticketcatalogue.security.UserDetailsImpl
import it.polito.ticketcatalogue.service.TicketCatalogueService
import it.polito.ticketcatalogue.service.TicketCatalogueServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class Consumer {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var ticketCatalogueService: TicketCatalogueServiceImpl

    @KafkaListener(topics = ["\${kafka.topics.result}"], groupId = "ppr")
     fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received {}", consumerRecord)
        ack.acknowledge()

        val result = consumerRecord.value() as ResultDTO
        val userDetails = UserDetailsImpl(
            result.userDetails.id,
            result.userDetails.username,
            result.userDetails.password, listOf(
            SimpleGrantedAuthority(result.userDetails.authorities as String?)
        ))
        val scope = CoroutineScope(Job())
        scope.launch {
            ticketCatalogueService.updateOrder(userDetails, result.orderId, result.result)
        }
    }
}
