package net.rainfords.demos.awsspringsqs

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS


@SpringBootTest
@Testcontainers
class BaseSqsIntegrationTest {

    companion object {
        private val LOCAL_STACK_VERSION = "localstack/localstack:2.3.2"

        @Container
        private val localStack: LocalStackContainer = LocalStackContainer(DockerImageName.parse(LOCAL_STACK_VERSION))

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.cloud.aws.region.static") { localStack.getRegion() }
            registry.add("spring.cloud.aws.credentials.access-key") { localStack.getAccessKey() }
            registry.add("spring.cloud.aws.credentials.secret-key") { localStack.getSecretKey() }
            registry.add("spring.cloud.aws.sqs.endpoint") {
                localStack.getEndpointOverride(SQS).toString()
            }
            // ...other AWS services endpoints can be added here
        }
    }
}

