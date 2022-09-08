package it.polito.payment.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment.dto.BuyTicketsDTO
import it.polito.payment.dto.OrderMessage
import it.polito.payment.dto.PaymentInfoDTO
import it.polito.payment.dto.UserOrder
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import java.sql.Timestamp


class ProductDeserializer : Deserializer<OrderMessage> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): OrderMessage {
        log.info("Deserializing...")
        //if(topic=="product"){
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
            val orderId: Long = orderNode.get("orderId").asLong()

            val price: Double = orderNode.get("totalPrice").asDouble()

            val payInfo = orderNode.get("paymentInfo")
            val creditCardNumber: String = payInfo.get("creditCardNumber").textValue()
            val exp : String = payInfo.get("exp").textValue()
            val cvv: String = payInfo.get("cvv").textValue()
            val cardHolder: String = payInfo.get("cardHolder").textValue()
            val paymentInfo = PaymentInfoDTO(creditCardNumber, exp, cvv, cardHolder)

            val buyTickets = orderNode.get("buyTickets");

            val cmd : String = buyTickets.get("cmd").textValue();
            val quantity :Int = buyTickets.get("quantity").asInt();
            val zones : String = buyTickets.get("zones").textValue();
            var type : String = buyTickets.get("type").textValue();

            var validitytime : Timestamp? = null;

            if (!buyTickets.get("validitytime").isNull){
                val timeLong= buyTickets.get("validitytime").asLong()
                validitytime = Timestamp(timeLong);
            }

            var maxnumberOfRides :Int? = null;

            if (!buyTickets.get("maxnumberOfRides").isNull) {
            maxnumberOfRides= buyTickets.get("maxnumberOfRides").asInt();
            }

            val buyTicketss= BuyTicketsDTO(cmd, quantity, zones, type, validitytime, maxnumberOfRides);

            val res = OrderMessage(userOrder, orderId, buyTicketss, price, paymentInfo)
            println("$res")

            //val res = objectMapper.readValue(str, OrderTopic::class.java)
            return res
        /*}
        else
            return null*/

    }

    override fun close() {}

}
