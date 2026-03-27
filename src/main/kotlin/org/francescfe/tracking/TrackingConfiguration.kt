package org.francescfe.tracking

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.message.TrackingStatusUpdated
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

@Configuration
class TrackingConfiguration {

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, DispatchPreparing>
    ): ConcurrentKafkaListenerContainerFactory<String, DispatchPreparing> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, DispatchPreparing>()
        factory.setConsumerFactory(consumerFactory)
        return factory
    }

    @Bean
    fun consumerFactory(
        @Value($$"${spring.kafka.bootstrap-servers}") bootstrapServers: String
    ): ConsumerFactory<String, DispatchPreparing> {
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java
        )

        val jsonDeserializer = JacksonJsonDeserializer(DispatchPreparing::class.java).apply {
            addTrustedPackages("org.francescfe.tracking.message")
            setUseTypeHeaders(false)
        }

        return DefaultKafkaConsumerFactory(
            config,
            StringDeserializer(),
            ErrorHandlingDeserializer(jsonDeserializer)
        )
    }

    @Bean
    fun producerFactory(
        @Value($$"${spring.kafka.bootstrap-servers}") bootstrapServers: String
    ): ProducerFactory<String, TrackingStatusUpdated> {
        val config = mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JacksonJsonSerializer::class.java
        )

        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(
        producerFactory: ProducerFactory<String, TrackingStatusUpdated>
    ): KafkaTemplate<String, TrackingStatusUpdated> = KafkaTemplate(producerFactory)
}
