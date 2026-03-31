package org.francescfe.tracking.integration

import org.awaitility.Awaitility.await
import org.francescfe.tracking.TrackingConfiguration
import org.francescfe.tracking.handler.DispatchTrackingHandler
import org.francescfe.tracking.message.DispatchCompleted
import org.francescfe.tracking.message.DispatchPreparing
import org.francescfe.tracking.message.Status
import org.francescfe.tracking.message.TrackingStatusUpdated
import org.francescfe.tracking.service.TrackingService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [
        TrackingConfiguration::class,
        TrackingService::class,
        DispatchTrackingHandler::class,
        TrackingIT.TestConfig::class,
    ]
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(
    controlledShutdown = true,
    partitions = 1,
    topics = ["dispatch.tracking", "tracking.status"]
)
class TrackingIT {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var testListener: KafkaTestListener

    @BeforeEach
    fun setUp() {
        val embeddedKafkaBroker = applicationContext.getBean<EmbeddedKafkaBroker>()
        val registry = applicationContext.getBean<KafkaListenerEndpointRegistry>()

        testListener.trackingStatusCounter.set(0)
        testListener.lastTrackingStatus = null

        registry.listenerContainers.forEach { container ->
            ContainerTestUtils.waitForAssignment(
                container,
                requireNotNull(container.containerProperties.topics).size * embeddedKafkaBroker.partitionsPerTopic
            )
        }
    }

    @Test
    fun `sending dispatch preparing publishes tracking status`() {
        val event = DispatchPreparing(orderId = randomUUID())

        kafkaTemplate.send("dispatch.tracking", event.orderId.toString(), event).get()

        await()
            .atMost(3, TimeUnit.SECONDS)
            .pollDelay(100, TimeUnit.MILLISECONDS)
            .until { testListener.trackingStatusCounter.get() == 1 }

        assertEquals(Status.PREPARING, testListener.lastTrackingStatus)
    }

    @Test
    fun `sending dispatch completed publishes completed tracking status`() {
        val event = DispatchCompleted(orderId = randomUUID(), date = "2026-03-31")

        kafkaTemplate.send("dispatch.tracking", event.orderId.toString(), event).get()

        await()
            .atMost(3, TimeUnit.SECONDS)
            .pollDelay(100, TimeUnit.MILLISECONDS)
            .until { testListener.trackingStatusCounter.get() == 1 }

        assertEquals(Status.COMPLETED, testListener.lastTrackingStatus)
    }

    @TestConfiguration
    @EnableKafka
    class TestConfig {

        @Bean
        fun testListener() = KafkaTestListener()
    }

    class KafkaTestListener {
        private val log = LoggerFactory.getLogger(javaClass)
        val trackingStatusCounter = AtomicInteger(0)
        @Volatile
        var lastTrackingStatus: Status? = null

        @KafkaListener(
            groupId = "tracking-integration-test",
            topics = ["tracking.status"],
            containerFactory = "kafkaListenerContainerFactory"
        )
        fun receiveTrackingStatus(@Payload payload: TrackingStatusUpdated) {
            log.debug("Received TrackingStatusUpdated {}", payload)
            lastTrackingStatus = payload.status
            trackingStatusCounter.incrementAndGet()
        }
    }
}
