package com.example.demo.send

import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Service

@Service
class SendJob : Job {
    @Produce("direct:sendNotification")
    lateinit var notificationProducer : ProducerTemplate

    override fun execute(context: JobExecutionContext?) {
        notificationProducer.sendBody(context?.jobDetail?.jobDataMap?.getString("text") ?: "No text found")
    }

}