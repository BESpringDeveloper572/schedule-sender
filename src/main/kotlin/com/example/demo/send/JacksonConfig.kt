package com.example.demo.send

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.module.kotlin.KotlinModule

@Configuration
class JacksonConfig {
    @Bean
    fun kotlinJsonCustomizer(): JsonMapperBuilderCustomizer {
        return JsonMapperBuilderCustomizer { builder ->
            builder.addModule(KotlinModule.Builder().build())
        }
    }
}