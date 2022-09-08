package it.polito.traveler.kafka

import it.polito.traveler.dto.ResultMessage
import it.polito.traveler.service.TravelerServiceImpl
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
    private lateinit var travelerService: TravelerServiceImpl

    @KafkaListener(topics = ["\${kafka.topics.result}"], groupId = "ppr1")
     fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        logger.info("Message received {}", consumerRecord)
        ack.acknowledge()

        val result = consumerRecord.value() as ResultMessage

        val ticket = result.buyTickets;

        val username = result.userDetails.username

        val res = result.result;

        val scope = CoroutineScope(Job())
        scope.launch {
            travelerService.buyTickets(res, username, ticket.quantity, ticket.zones, ticket.type, ticket.validitytime, ticket.maxnumberOfRides)
        }
    }
}
