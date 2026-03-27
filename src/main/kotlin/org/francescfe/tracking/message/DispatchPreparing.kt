package org.francescfe.tracking.message

import java.util.UUID

data class DispatchPreparing(
    val orderId: UUID
)
