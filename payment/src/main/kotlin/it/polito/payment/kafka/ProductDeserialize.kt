package it.polito.payment.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.payment.dto.OrderTopic
import it.polito.payment.dto.PaymentInfoDTO
import it.polito.payment.dto.UserOrder
import it.polito.payment.security.UserDetailsImpl
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<OrderTopic> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): OrderTopic {
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

            val res = OrderTopic(userOrder, orderId, price, paymentInfo)
            println("$res")

            //val res = objectMapper.readValue(str, OrderTopic::class.java)
            return res
        /*}
        else
            return null*/

    }

    override fun close() {}

}
