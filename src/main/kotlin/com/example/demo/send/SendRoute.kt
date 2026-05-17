package com.example.demo.send

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class SendRoute(
    @Value("\${send-notification-exchange}")
    val sendNotificationExchange: String
) : RouteBuilder() {
    override fun configure() {
        from("direct:sendNotification")
            .marshal().json()
            .to(
                UriComponentsBuilder.newInstance()
                    .scheme("spring-rabbitmq")
                    .schemeSpecificPart(sendNotificationExchange)
                    .build()
                    .toUriString()
            )

    }
}