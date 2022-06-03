package it.polito.login.handlerinterceptor

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class RateLimiterInterceptor : HandlerInterceptor {

    var refill = Refill.intervally(10, Duration.ofSeconds(1))
    var limit = Bandwidth.classic(10, refill)
    var bucket: Bucket = Bucket.builder()
        .addLimit(limit)
        .build()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if(!bucket.tryConsume(1)) {
            //println("Bucket empty")
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "")
        } else {
            //println("Bucket remaining: ${bucket.availableTokens}")
        }
        return super.preHandle(request, response, handler)
    }
}