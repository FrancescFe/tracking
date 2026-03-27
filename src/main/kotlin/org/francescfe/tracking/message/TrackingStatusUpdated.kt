package org.francescfe.tracking.message

import java.util.UUID

data class TrackingStatusUpdated(
    val orderId: UUID,
    val status: Status
)
