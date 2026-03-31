package org.francescfe.tracking.service

import org.francescfe.tracking.message.DispatchCompleted
import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.message.Status
import org.francescfe.tracking.message.TrackingStatusUpdated
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TrackingService(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    companion object {
        private const val TRACKING_STATUS_TOPIC = "tracking.status"
    }

    fun process(payload: DispatchPreparing) {
        publishTrackingStatus(payload.orderId.toString(), payload.orderId, Status.PREPARING)
    }

    fun process(payload: DispatchCompleted) {
        publishTrackingStatus(payload.orderId.toString(), payload.orderId, Status.COMPLETED)
    }

    private fun publishTrackingStatus(
        key: String,
        orderId: UUID,
        status: Status
    ) {
        val event = TrackingStatusUpdated(
            orderId = orderId,
            status = status
        )

        kafkaTemplate.send(TRACKING_STATUS_TOPIC, key, event)
    }
}
