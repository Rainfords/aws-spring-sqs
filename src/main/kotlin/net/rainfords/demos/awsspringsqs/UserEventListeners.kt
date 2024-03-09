package net.rainfords.demos.awsspringsqs

import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.SqsHeaders.MessageSystemAttributes.SQS_APPROXIMATE_FIRST_RECEIVE_TIMESTAMP
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import java.util.*


@Component
class UserEventListeners // Our listeners will be added here
    (private val userRepository: UserRepository) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserEventListeners::class.java)

        const val EVENT_TYPE_CUSTOM_HEADER: String = "eventType"
    }

    @SqsListener("\${events.queues.user-created-by-name-queue}")
    fun receiveStringMessage(username: String) {
        logger.info("Received message: {}", username)
        userRepository.save(
            User(
                UUID.randomUUID().toString(), username, "${username}@email.com"
            )
        )
    }

    @SqsListener("\${events.queues.user-created-record-queue}")
    fun receiveRecordMessage(event: UserCreatedEvent) {
        logger.info("Received message: {}", event)
        userRepository.save(User(event.id, event.username, event.email))
    }

    @SqsListener("\${events.queues.user-created-event-type-queue}")
    fun customHeaderMessage(
        message: Message<UserCreatedEvent?>, @Header(EVENT_TYPE_CUSTOM_HEADER) eventType: String?,
        @Header(
            SQS_APPROXIMATE_FIRST_RECEIVE_TIMESTAMP
        ) firstReceive: Long?
    ) {
        logger.info(
            "Received message {} with event type {}. First received at approximately {}.",
            message,
            eventType,
            firstReceive
        )
        val payload: UserCreatedEvent = message.payload
        userRepository.save(User(payload.id, payload.username, payload.email))
    }
}
