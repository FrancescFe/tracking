package org.francescfe.tracking.handler

import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.service.TrackingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.UUID.randomUUID

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
}
