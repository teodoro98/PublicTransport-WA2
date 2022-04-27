package it.polito.server.handlerinterceptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        val rateLimiterInterceptor = RateLimiterInterceptor()
        registry.addInterceptor(rateLimiterInterceptor)
            .addPathPatterns("/users/register")
    }
}