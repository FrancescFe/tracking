package org.francescfe.tracking.service

import org.francescfe.tracking.message.DispatchCompleted
import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.message.Status
import org.francescfe.tracking.message.TrackingStatusUpdated
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class TrackingServiceTest {

    private lateinit var service: TrackingService
    @Mock
    private lateinit var kafkaTemplateMock: KafkaTemplate<String, Any>

    @BeforeEach
    fun setUp() {
        service = TrackingService(kafkaTemplateMock)
    }

    @Test
    fun `process publishes PREPARING status to tracking status topic`() {
        val orderId = randomUUID()
        val payload = DispatchPreparing(orderId)

        service.process(payload)

        val eventCaptor = ArgumentCaptor.forClass(TrackingStatusUpdated::class.java)
        verify(kafkaTemplateMock).send(
            eq("tracking.status"),
            eq(orderId.toString()),
            eventCaptor.capture()
        )

        val publishedEvent = eventCaptor.value
        assertEquals(orderId, publishedEvent.orderId)
        assertEquals(Status.PREPARING, publishedEvent.status)
    }

    @Test
    fun `process rethrows when kafka producer fails`() {
        val orderId = randomUUID()
        val payload = DispatchPreparing(orderId)
        doThrow(RuntimeException("Producer failure")).`when`(kafkaTemplateMock).send(
            eq("tracking.status"),
            eq(orderId.toString()),
            any(TrackingStatusUpdated::class.java)
        )

        val exception = assertFailsWith<RuntimeException> {
            service.process(payload)
        }

        verify(kafkaTemplateMock).send(
            eq("tracking.status"),
            eq(orderId.toString()),
            any(TrackingStatusUpdated::class.java)
        )
        assertEquals("Producer failure", exception.message)
    }

    @Test
    fun `process completed publishes COMPLETED status to tracking status topic`() {
        val orderId = randomUUID()
        val payload = DispatchCompleted(orderId, "2026-03-31")

        service.process(payload)

        val eventCaptor = ArgumentCaptor.forClass(TrackingStatusUpdated::class.java)
        verify(kafkaTemplateMock).send(
            eq("tracking.status"),
            eq(orderId.toString()),
            eventCaptor.capture()
        )

        val publishedEvent = eventCaptor.value
        assertEquals(orderId, publishedEvent.orderId)
        assertEquals(Status.COMPLETED, publishedEvent.status)
    }
}
