package net.rainfords.demos.awsspringsqs

import io.awspring.cloud.sqs.operations.SqsSendOptions
import io.awspring.cloud.sqs.operations.SqsTemplate
import net.rainfords.demos.awsspringsqs.UserEventListeners.Companion.EVENT_TYPE_CUSTOM_HEADER
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.*


class SpringCloudAwsSQSLiveTest(
    @Autowired
    private val sqsTemplate: SqsTemplate,
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val eventQueuesProperties: EventQueuesProperties
) : BaseSqsIntegrationTest() {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SpringCloudAwsSQSLiveTest::class.java)
    }

    @Test
    fun givenAStringPayload_whenSend_shouldReceive() {
        // given
        val userName = "Albert"

        // when
        sqsTemplate.send<Any?> { to: SqsSendOptions<Any?> ->
            to.queue(eventQueuesProperties.userCreatedByNameQueue)
                .payload(userName)
        }
        logger.info("Message sent with payload {}", userName)

        // then
        await().atMost(Duration.ofSeconds(3))
            .until {
                userRepository.findByName(userName).isPresent
            }
    }

    @Test
    fun givenARecordPayload_whenSend_shouldReceive() {
        // given
        val userId = UUID.randomUUID().toString()
        val payload = UserCreatedEvent(userId, "John", "john@email.com")

        // when
        sqsTemplate.send { to: SqsSendOptions<Any?> ->
            to.queue(eventQueuesProperties.userCreatedRecordQueue)
                .payload(payload)
        }

        // then
        logger.info("Message sent with payload: {}", payload)
        await().atMost(Duration.ofSeconds(3))
            .until {
                userRepository.findById(userId).isPresent
            }
    }

    @Test
    fun givenCustomHeaders_whenSend_shouldReceive() {
        // given
        val userId = UUID.randomUUID()
            .toString()
        val payload = UserCreatedEvent(userId, "John", "john@baeldung.com")
        val headers = mapOf(EVENT_TYPE_CUSTOM_HEADER to "UserCreatedEvent")

        // when
        sqsTemplate.send { to: SqsSendOptions<Any?> ->
            to.queue(eventQueuesProperties.userCreatedEventTypeQueue)
                .payload(payload)
                .headers(headers)
        }

        // then
        logger.info(
            "Sent message with payload {} and custom headers: {}",
            payload,
            headers
        )
        await().atMost(Duration.ofSeconds(3))
            .until {
                userRepository.findById(userId).isPresent
            }
    }
}