package org.francescfe.tracking.service

import org.francescfe.tracking.message.DispatchPreparing
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TrackingService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun process(payload: DispatchPreparing) {
        log.info("Processing dispatch tracking payload: {}", payload)
    }
}
