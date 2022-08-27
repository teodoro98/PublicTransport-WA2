package it.polito.login.handlerinterceptor

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        val rateLimiterInterceptor = RateLimiterInterceptor()
        registry.addInterceptor(rateLimiterInterceptor)
            .addPathPatterns("/users/**")
    }
}