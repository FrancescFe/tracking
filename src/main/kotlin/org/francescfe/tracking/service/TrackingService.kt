package org.francescfe.tracking.service

import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.message.Status
import org.francescfe.tracking.message.TrackingStatusUpdated
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class TrackingService(
    private val kafkaTemplate: KafkaTemplate<String, TrackingStatusUpdated>
) {

    fun process(payload: DispatchPreparing) {
        val event = TrackingStatusUpdated(
            orderId = payload.orderId,
            status = Status.PREPARING
        )

        kafkaTemplate.send("tracking.status", payload.orderId.toString(), event)
    }
}
