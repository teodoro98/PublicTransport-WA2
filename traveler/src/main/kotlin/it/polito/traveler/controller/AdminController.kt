package it.polito.traveler.controller

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.service.TravelerServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController {

    @Autowired
    private lateinit var travelerService: TravelerServiceImpl

    @GetMapping("/travelers")
    @ResponseStatus(HttpStatus.FOUND)
    fun getTravelers(id:Long): List<UserDetailsDTO>{
        return travelerService.getTravelers()
    }

    @GetMapping("/traveler/{userID}/profile")
    fun getTravelerProfile(@PathVariable userID: Long): UserDetailsDTO{
        return travelerService.getProfile(userID)
    }

    @GetMapping("/traveler/{userID}/tickets")
    fun getTravelerTickets(@PathVariable userID: Long): List<TicketPurchasedDTO>{
        return travelerService.getTickets(userID)
    }

}