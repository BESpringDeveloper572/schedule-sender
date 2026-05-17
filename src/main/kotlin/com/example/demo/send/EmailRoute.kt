package com.example.demo.send

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class EmailRoute(
    @Value("\${send-notification-exchange}")
    val sendNotificationExchange: String,
    @Value("\${email.queue}")
    val emailQueue: String,
    @Value("\${email.max-workers}")
    val emailWorkers: Int,
    @Value("\${email.max-messages-size}")
    val emailProcessingMaxMessages: Int,
    @Value("\${email.polling-interval}")
    val pollingInterval: Int
) : RouteBuilder() {
    override fun configure() {
        from(
            UriComponentsBuilder.newInstance()
                .scheme("scheduler")
                .path("emailPoller")
                .queryParam("delay", pollingInterval)
                .build()
                .toUriString()
        )
            .pollEnrich(
                UriComponentsBuilder.newInstance()
                    .scheme("spring-rabbitmq")
                    .path(sendNotificationExchange)
                    .queryParam("queues", emailQueue)
                    .queryParam("concurrentConsumers", emailWorkers)
                    .queryParam("prefetchCount", emailProcessingMaxMessages)
                    .build()
                    .toUriString()
            )
            .unmarshal().json(JsonLibrary.Jackson, SendMessage::class.java)
            .log("Sending email message: \${body}")
    }
}