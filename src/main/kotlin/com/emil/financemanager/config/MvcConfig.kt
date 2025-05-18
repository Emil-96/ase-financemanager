package com.emil.financemanager.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.boot.web.servlet.view.MustacheViewResolver

@Configuration
class MvcConfig : WebMvcConfigurer {
    
    @Bean
    fun mustacheViewResolver(): MustacheViewResolver {
        val resolver = MustacheViewResolver()
        resolver.setPrefix("classpath:/templates/")
        resolver.setSuffix(".mustache")
        resolver.setExposeRequestAttributes(true)
        resolver.setContentType("text/html;charset=UTF-8")
        return resolver
    }
    
    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.viewResolver(mustacheViewResolver())
    }
    
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
    }
} 