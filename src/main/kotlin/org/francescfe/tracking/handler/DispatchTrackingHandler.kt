package org.francescfe.tracking.handler

import org.francescfe.tracking.message.DispatchTracking
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class DispatchTrackingHandler {

    @KafkaListener(
        id = "dispatchTrackingConsumerClient",
        topics = ["dispatch.tracking"],
        groupId = "tracking.dispatch.tracking.consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(payload: DispatchTracking) {
        println("Received dispatch tracking payload: $payload")
    }
}