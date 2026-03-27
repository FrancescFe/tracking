package org.francescfe.tracking.message

import java.util.UUID

data class DispatchTracking(
    val orderId: UUID,
    val status: String
)