package it.polito.turnstile.controller

import it.polito.turnstile.dto.QRCode
import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.dto.TurnstileDetailsDTO
import it.polito.turnstile.security.UserDetailsImpl
import it.polito.turnstile.service.TurnstileService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDateTime


@RestController
class TurnstileController {

    @Autowired
    private lateinit var turnstileService: TurnstileService

    @GetMapping("turnstile/se")
    @PreAuthorize("hasRole('TURNSTILE')")
    @ResponseStatus(HttpStatus.FOUND)
    suspend fun getSecret(): String{
        return turnstileService.getSecret()
    }

    @GetMapping("turnstile/details/{turnstileUsername}")
    @ResponseStatus(HttpStatus.FOUND)
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun getTurnstileDetails(@PathVariable("turnstileUsername") turnstileUsername: String): TurnstileDetailsDTO {
        return turnstileService.getTurnstileDetails(turnstileUsername)
    }

    @GetMapping("turnstile/details")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('TURNSTILE')")
    suspend fun getTurnstileDetails(principal: Principal): TurnstileDetailsDTO {
        val userDetails: UserDetailsImpl = (principal as UsernamePasswordAuthenticationToken).principal as UserDetailsImpl
        return turnstileService.getTurnstileDetails(userDetails.username)
    }

    @PostMapping("turnstile/details")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun addDetails(@RequestBody details: TurnstileDetailsDTO) {
        turnstileService.addDetails(details)
    }

    @PostMapping("turnstile/checkTicket")
    @PreAuthorize("hasRole('TURNSTILE')")
    @ResponseStatus(HttpStatus.OK)
    suspend fun checkTicket(@RequestBody qrCode: QRCode, principal: Principal){
        val userDetails: UserDetailsImpl = (principal as UsernamePasswordAuthenticationToken).principal as UserDetailsImpl
        return turnstileService.checkTicket(qrCode.jwt, userDetails.username)
    }

    @GetMapping("admin/transits")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTransits(@RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            since: LocalDateTime?,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            to: LocalDateTime?
    ): Flow<TransitDTO> {
        return turnstileService.getTransits(since, to, null)
    }

    @GetMapping("admin/transits/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.FOUND)
    suspend fun getTransits(@PathVariable("username") username: String,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            since: LocalDateTime?,
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            to: LocalDateTime?
    ): Flow<TransitDTO> {
        return turnstileService.getTransits(since, to, username)
    }
}