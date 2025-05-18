package com.emil.financemanager.config

import com.samskivert.mustache.Mustache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class MustacheConfig {

    @Bean
    fun mustacheCompiler(
        environment: Environment,
        mustacheTemplateLoader: Mustache.TemplateLoader?
    ): Mustache.Compiler {
        val compiler = Mustache.compiler()
            .withLoader(mustacheTemplateLoader)
        
        // Add custom helpers
        return compiler.withFormatter { value ->
            when (value) {
                is Number -> String.format("%.2f", value.toDouble())
                else -> value.toString()
            }
        }
    }
} 