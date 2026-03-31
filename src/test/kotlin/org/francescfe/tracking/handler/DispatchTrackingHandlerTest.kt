package org.francescfe.tracking.handler

import org.francescfe.tracking.message.DispatchCompleted
import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.service.TrackingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.UUID.randomUUID
import kotlin.test.assertFailsWith

class DispatchTrackingHandlerTest {

    private lateinit var handler: DispatchTrackingHandler
    private lateinit var trackingServiceMock: TrackingService

    @BeforeEach
    fun setUp() {
        trackingServiceMock = mock(TrackingService::class.java)
        handler = DispatchTrackingHandler(trackingServiceMock)
    }

    @Test
    fun `listen delegates payload to tracking service`() {
        val testEvent = DispatchPreparing(randomUUID())

        handler.listen(testEvent)

        verify(trackingServiceMock).process(testEvent)
    }

    @Test
    fun `listen rethrows when tracking service fails`() {
        val testEvent = DispatchPreparing(randomUUID())
        doThrow(RuntimeException("Service failure")).`when`(trackingServiceMock).process(testEvent)

        assertFailsWith<RuntimeException> {
            handler.listen(testEvent)
        }

        verify(trackingServiceMock).process(testEvent)
    }

    @Test
    fun `listen delegates completed payload to tracking service`() {
        val testEvent = DispatchCompleted(randomUUID(), "2026-03-31")

        handler.listen(testEvent)

        verify(trackingServiceMock).process(testEvent)
    }

    @Test
    fun `listen completed rethrows when tracking service fails`() {
        val testEvent = DispatchCompleted(randomUUID(), "2026-03-31")
        doThrow(RuntimeException("Service failure")).`when`(trackingServiceMock).process(testEvent)

        assertFailsWith<RuntimeException> {
            handler.listen(testEvent)
        }

        verify(trackingServiceMock).process(testEvent)
    }
}
