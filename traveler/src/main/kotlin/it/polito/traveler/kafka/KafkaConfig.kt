package it.polito.traveler.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaConfig(
    @Value("\${kafka.bootstrapAddress}")
    private val servers: String,
) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

}
