package org.francescfe.tracking.handler

import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.service.TrackingService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class DispatchTrackingHandler(
    private val trackingService: TrackingService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        id = "dispatchTrackingConsumerClient",
        topics = ["dispatch.tracking"],
        groupId = "tracking.dispatch.tracking.consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(payload: DispatchPreparing) {
        log.info("Received dispatch tracking payload: {}", payload)
        trackingService.process(payload)
    }
}
