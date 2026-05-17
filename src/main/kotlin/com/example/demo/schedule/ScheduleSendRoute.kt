package com.example.demo.schedule

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ScheduleSendRoute : RouteBuilder()  {
    override fun configure() {
        from("direct:scheduleMessage")
            .log("Received message for scheduling: \${body}")
            .bean(JobHandler::class.java, "schedule")
    }

}