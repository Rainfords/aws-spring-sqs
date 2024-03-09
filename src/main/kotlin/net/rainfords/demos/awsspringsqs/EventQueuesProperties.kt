package net.rainfords.demos.awsspringsqs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "events.queues")
class EventQueuesProperties(
    val userCreatedByNameQueue: String,
    val userCreatedRecordQueue: String,
    val userCreatedEventTypeQueue: String
)