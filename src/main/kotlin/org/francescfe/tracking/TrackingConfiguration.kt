package org.francescfe.tracking

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.francescfe.tracking.message.DispatchTracking
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer

@Configuration
class TrackingConfiguration {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, DispatchTracking>
    ): ConcurrentKafkaListenerContainerFactory<String, DispatchTracking> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, DispatchTracking>()
        factory.setConsumerFactory(consumerFactory)
        return factory
    }

    @Bean
    fun consumerFactory(
        @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String
    ): ConsumerFactory<String, DispatchTracking> {
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java
        )

        val jsonDeserializer = JacksonJsonDeserializer(DispatchTracking::class.java).apply {
            addTrustedPackages("org.francescfe.tracking.message")
            setUseTypeHeaders(false)
        }

        return DefaultKafkaConsumerFactory(
            config,
            StringDeserializer(),
            ErrorHandlingDeserializer(jsonDeserializer)
        )
    }
}
