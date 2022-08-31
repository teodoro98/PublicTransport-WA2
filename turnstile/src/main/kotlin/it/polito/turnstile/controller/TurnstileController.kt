package it.polito.turnstile.controller

import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.service.TurnstileService
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
class TurnstileController {

    @Autowired
    private lateinit var turnstileService: TurnstileService

    @GetMapping("turnstile/se")
    @ResponseStatus(HttpStatus.FOUND)
    suspend fun getSecret(): String{

        //val userDetails: UserDetailsImpl =
        //    SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        return turnstileService.getSecret()
    }

    @PostMapping("turnstile/checkTicket")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun checkTicket(@RequestBody qrCode: String): Boolean{


        return turnstileService.checkTicket(qrCode)
    }

    @GetMapping("admin/transits")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.FOUND)
    suspend fun getTransits(): Flow<TransitDTO> {

        //SUPER ADMIN???
        return turnstileService.getTransits()
    }
}