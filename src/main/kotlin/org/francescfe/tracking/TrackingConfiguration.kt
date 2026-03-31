package org.francescfe.tracking

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.francescfe.tracking.message.DispatchCompleted
import org.francescfe.tracking.message.DispatchPreparing
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer
import org.springframework.kafka.support.serializer.JacksonJsonSerializer

private const val TRUSTED_PACKAGES = "org.francescfe.tracking.message,org.francescfe.dispatch.message"
private val TYPE_MAPPINGS =
    "org.francescfe.dispatch.message.DispatchPreparing:${DispatchPreparing::class.qualifiedName}," +
        "org.francescfe.dispatch.message.DispatchCompleted:${DispatchCompleted::class.qualifiedName}"

@Configuration
class TrackingConfiguration {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.setConsumerFactory(consumerFactory)
        return factory
    }

    @Bean
    fun consumerFactory(
        @Value($$"${spring.kafka.bootstrap-servers}") bootstrapServers: String
    ): ConsumerFactory<String, Any> {
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JacksonJsonDeserializer::class.java,
            JacksonJsonDeserializer.TRUSTED_PACKAGES to TRUSTED_PACKAGES,
            JacksonJsonDeserializer.TYPE_MAPPINGS to TYPE_MAPPINGS
        )
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun producerFactory(
        @Value($$"${spring.kafka.bootstrap-servers}") bootstrapServers: String
    ): ProducerFactory<String, Any> {
        val config = mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JacksonJsonSerializer::class.java
        )

        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(
        producerFactory: ProducerFactory<String, Any>
    ): KafkaTemplate<String, Any> = KafkaTemplate(producerFactory)
}
