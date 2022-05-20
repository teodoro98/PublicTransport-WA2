package it.polito.traveler.controller

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.repository.UserDetailsRepository
import it.polito.traveler.service.TravelerServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController {

    @Autowired
    private lateinit var travelerService: TravelerServiceImpl

    @GetMapping("/travelers")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.FOUND)
    fun getTravelers(): List<UserDetailsDTO>{
        val auth: Authentication = SecurityContextHolder.getContext().getAuthentication()

        return travelerService.getTravelers()
    }

    @GetMapping("/traveler/{userID}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    fun getTravelerProfile(@PathVariable userID: Long): UserDetailsDTO{
        return travelerService.getProfile(userID)
    }

    @GetMapping("/traveler/{userID}/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    fun getTravelerTickets(@PathVariable userID: Long): List<TicketPurchasedDTO>{
        return travelerService.getTickets(userID)
    }

}