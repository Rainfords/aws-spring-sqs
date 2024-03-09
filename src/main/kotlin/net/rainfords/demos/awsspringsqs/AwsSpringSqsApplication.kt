package net.rainfords.demos.awsspringsqs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(EventQueuesProperties::class)
class AwsSpringSqsApplication

fun main(args: Array<String>) {
    runApplication<AwsSpringSqsApplication>(*args)
}
