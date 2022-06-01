package it.polito.payment.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment.dto.OrderTopic
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<OrderTopic> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): OrderTopic? {
        log.info("Deserializing...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to Order"), UTF_8
            ), OrderTopic::class.java
        )
    }

    override fun close() {}

}
