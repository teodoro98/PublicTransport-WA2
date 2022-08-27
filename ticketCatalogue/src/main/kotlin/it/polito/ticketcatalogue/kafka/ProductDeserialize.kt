package it.polito.ticketcatalogue.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.ticketcatalogue.dto.ResultDTO
import it.polito.ticketcatalogue.dto.UserOrder
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<ResultDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): ResultDTO? {
        log.info("Deserializing...")
        if(topic=="result"){
            if(data == null) {
                throw SerializationException("Error when deserializing byte[] to Order")
            }
            val str = String(data)

            val orderNode = ObjectMapper().readTree(str)
            val id : Long = orderNode.get("userDetails").get("id").asLong()
            val username: String = orderNode.get("userDetails").get("username").textValue()
            val password: String = orderNode.get("userDetails").get("username").textValue()
            val authorities: String = orderNode.get("userDetails").get("authorities").textValue()
            val userOrder = UserOrder(id, username, password, authorities)

            val orderId : Long = orderNode.get("orderId").asLong()
            val result: Boolean = orderNode.get("result").asBoolean()
            val resultDTO = ResultDTO(userOrder, orderId, result)

            val res = resultDTO
            return res
        }
        else
            return null

    }

    override fun close() {}

}
