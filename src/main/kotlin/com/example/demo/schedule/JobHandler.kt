package com.example.demo.schedule

import com.example.demo.mvc.SendRequest
import com.example.demo.send.SendJob
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.ZoneId
import java.util.*

@Service
class JobHandler(val scheduler: Scheduler) {

    fun schedule(jobParams: JobParams): Mono<ScheduleResponse> {
        val jobKey = JobKey.jobKey(UUID.randomUUID().toString())
        val job = JobBuilder.newJob(SendJob::class.java)
                .storeDurably()
                .usingJobData("text", jobParams.sendRequest.text)
                .usingJobData("userId", jobParams.userId)
                .withIdentity(jobKey)
                .withDescription("Schedule send message to through all methods")
                .build()
        val zoneId = Optional.ofNullable(jobParams)
            .map(JobParams::sendRequest)
            .map(SendRequest::timeZone)
            .map(ZoneId::of)
            .orElse(ZoneId.systemDefault())
        val timestamp = Instant.ofEpochMilli(jobParams.sendRequest.timestamp)
            .atZone(zoneId)
        val runOnceTrigger = TriggerBuilder.newTrigger()
                .startAt(Date.from(timestamp.toInstant()))
                .build()
        val jobDate = scheduler.scheduleJob(job, runOnceTrigger)
        return Mono.just(ScheduleResponse("ok", jobDate))
    }
}
