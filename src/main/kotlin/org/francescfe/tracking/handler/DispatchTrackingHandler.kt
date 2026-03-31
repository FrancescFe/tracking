package org.francescfe.tracking.handler

import org.francescfe.tracking.message.DispatchCompleted
import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.service.TrackingService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@KafkaListener(
    id = "dispatchTrackingConsumerClient",
    topics = ["dispatch.tracking"],
    groupId = "tracking.dispatch.tracking.consumer",
    containerFactory = "kafkaListenerContainerFactory"
)
class DispatchTrackingHandler(
    private val trackingService: TrackingService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaHandler
    fun listen(payload: DispatchPreparing) {
        log.info("Received dispatch preparing payload: {}", payload)
        try {
            trackingService.process(payload)
        } catch (exception: Exception) {
            log.error("Processing failure for dispatch preparing payload: {}", payload, exception)
            throw exception
        }
    }

    @KafkaHandler
    fun listen(payload: DispatchCompleted) {
        log.info("Received dispatch completed payload: {}", payload)
        try {
            trackingService.process(payload)
        } catch (exception: Exception) {
            log.error("Processing failure for dispatch completed payload: {}", payload, exception)
            throw exception
        }
    }

    @KafkaHandler(isDefault = true)
    fun listen(payload: Any) {
        log.info("Received dispatch tracking payload: {}", payload)
    }
}
