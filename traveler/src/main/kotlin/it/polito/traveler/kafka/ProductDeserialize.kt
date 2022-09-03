package it.polito.traveler.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.traveler.dto.BuyTicketsDTO
import it.polito.traveler.dto.ResultMessage
import it.polito.traveler.dto.UserOrder
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<ResultMessage> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): ResultMessage? {
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


            val buyTickets = orderNode.get("buyTickets");

            val cmd : String = buyTickets.get("cmd").textValue();
            val quantity :Int = buyTickets.get("quantity").asInt();
            val zones : String = buyTickets.get("zones").textValue();
            var type : String = buyTickets.get("type").textValue();

            var validitytime : Timestamp? = null;

            if (!buyTickets.get("validitytime").isNull){
                validitytime = Timestamp(buyTickets.get("validitytime").asLong());
            }

            var maxnumberOfRides :Int? = null;

            if (!buyTickets.get("maxnumberOfRides").isNull) {
                maxnumberOfRides= buyTickets.get("maxnumberOfRides").asInt();
            }

            val buyTicketss= BuyTicketsDTO(cmd, quantity, zones, type, validitytime, maxnumberOfRides);

            val resultDTO = ResultMessage(userOrder, orderId, buyTicketss, result)

            return resultDTO
        }
        else
            return null

    }

    override fun close() {}

}
