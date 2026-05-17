package com.example.demo.send

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class SmsRoute(
    @Value("\${send-notification-exchange}")
    val sendNotificationExchange: String,
    @Value("\${sms.queue}")
    val smsQueue: String,
    @Value("\${sms.max-workers}")
    val smsWorkers: Int,
    @Value("\${sms.max-messages-size}")
    val smsProcessingMaxMessages: Int,
    @Value("\${sms.polling-interval}")
    val pollingInterval: Int
) : RouteBuilder() {
    override fun configure() {
        from(
            UriComponentsBuilder.newInstance()
                .scheme("scheduler")
                .path("smsPoller")
                .queryParam("delay", pollingInterval)
                .build()
                .toUriString()
        )
            .pollEnrich(
                UriComponentsBuilder.newInstance()
                    .scheme("spring-rabbitmq")
                    .path(sendNotificationExchange)
                    .queryParam("queues", smsQueue)
                    .queryParam("concurrentConsumers", smsWorkers)
                    .queryParam("prefetchCount", smsProcessingMaxMessages)
                    .build()
                    .toUriString()
            )
            .unmarshal().json(JsonLibrary.Jackson, SendMessage::class.java)
            .log("Sending sms message: \${body}")
    }
}