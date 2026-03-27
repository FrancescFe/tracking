package org.francescfe.tracking.service

import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.message.Status
import org.francescfe.tracking.message.TrackingStatusUpdated
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class TrackingServiceTest {

    private lateinit var service: TrackingService
    private lateinit var kafkaTemplate: KafkaTemplate<String, TrackingStatusUpdated>

    @BeforeEach
    fun setUp() {
        @Suppress("UNCHECKED_CAST")
        val template = mock(KafkaTemplate::class.java) as KafkaTemplate<String, TrackingStatusUpdated>
        kafkaTemplate = template
        service = TrackingService(kafkaTemplate)
    }

    @Test
    fun `process publishes PREPARING status to tracking status topic`() {
        val orderId = randomUUID()
        val payload = DispatchPreparing(orderId)

        service.process(payload)

        val eventCaptor = ArgumentCaptor.forClass(TrackingStatusUpdated::class.java)
        verify(kafkaTemplate).send(
            eq("tracking.status"),
            eq(orderId.toString()),
            eventCaptor.capture()
        )

        val publishedEvent = eventCaptor.value
        assertEquals(orderId, publishedEvent.orderId)
        assertEquals(Status.PREPARING, publishedEvent.status)
    }
}
