package it.polito.ticketcatalogue.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticketcatalogue.dto.OrderMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory


class ProductSerializer : Serializer<OrderMessage> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: OrderMessage?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing OrderTopic to ByteArray[]")
        )
    }

    override fun close() {}
}
