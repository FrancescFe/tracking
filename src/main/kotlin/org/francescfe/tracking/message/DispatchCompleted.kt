package org.francescfe.tracking.message

import java.util.UUID

data class DispatchCompleted(
    val orderId: UUID,
    val date: String
)
