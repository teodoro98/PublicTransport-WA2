package it.polito.payment.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment.dto.ResultMessage
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory


class ProductSerializer : Serializer<ResultMessage> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: ResultMessage?): ByteArray? {
        log.info("Serializing...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing OrderTopic to ByteArray[]")
        )
    }

    override fun close() {}
}
