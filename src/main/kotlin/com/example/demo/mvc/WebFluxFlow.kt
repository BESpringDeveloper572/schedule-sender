package com.example.demo.mvc

import com.example.demo.schedule.JobParams
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ResolvableType
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.integration.camel.dsl.Camel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.http.support.DefaultHttpHeaderMapper
import org.springframework.integration.webflux.dsl.WebFlux
import reactor.core.publisher.Mono

@Configuration
class WebFluxFlow {
    @Bean
    fun scheduleEndpoint() : IntegrationFlow {
        return integrationFlow(
            WebFlux.inboundGateway("/schedule")
                .apply {
                    requestMapping { m -> m.methods(HttpMethod.POST) }
                    requestPayloadType(ResolvableType.forClassWithGenerics(Mono::class.java, SendRequest::class.java))
                    headerMapper(DefaultHttpHeaderMapper().apply {
                        setInboundHeaderNames("userId", "HTTP_REQUEST_HEADERS")
                    })
                    statusCodeFunction { _ -> HttpStatus.CREATED }
                })
        {
            fluxTransform<SendRequest, JobParams> { flux ->
                flux.map { message ->
                    val userId = message.headers["userId"]?.toString() ?: "unknown"
                    JobParams(message.payload, userId)
                }
            }
            handle(Camel.gateway().endpointUri("direct:scheduleMessage"))
        }
    }
}