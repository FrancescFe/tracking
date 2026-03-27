package org.francescfe.tracking.handler

import org.francescfe.tracking.message.DispatchTracking
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class DispatchTrackingHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        id = "dispatchTrackingConsumerClient",
        topics = ["dispatch.tracking"],
        groupId = "tracking.dispatch.tracking.consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(payload: DispatchTracking) {
        log.info("Received dispatch tracking payload: {}", payload)
    }
}