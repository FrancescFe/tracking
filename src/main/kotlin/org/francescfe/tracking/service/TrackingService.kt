package org.francescfe.tracking.service

import org.francescfe.tracking.message.DispatchTracking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TrackingService {

    private val log = LoggerFactory.getLogger(javaClass)

    fun process(event: DispatchTracking) {
        log.info("Processing dispatch tracking event: $event")
    }
}