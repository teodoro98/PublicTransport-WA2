package it.polito.traveler.controller

import it.polito.traveler.dto.BuyTickets
import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.security.UserDetailsImpl
import it.polito.traveler.service.TravelerServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/my")
class UserDetailsController {


    @Autowired
    private lateinit var travelerService: TravelerServiceImpl


    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.FOUND)
    fun getProfile(): UserDetailsDTO{
        //id from login security
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        return travelerService.getProfile(userDetails.username)
    }

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun updateProfile(@RequestBody user : UserDetailsDTO){
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val username = userDetails.username
        travelerService.updateProfile(user, username)
    }

    @GetMapping("/tickets")
    @ResponseStatus(HttpStatus.FOUND)
    fun getTickets(): List<TicketPurchasedDTO>{
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val username = userDetails.username
        return travelerService.getTickets(username)
    }

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.ACCEPTED)

    fun buyTickets(@RequestBody buyTickets: BuyTickets): List<TicketPurchasedDTO>{
        if(buyTickets.cmd != "buy_tickets") {
            throw CmdNotValid()
        }
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val username = userDetails.username
        val tickets = travelerService.buyTickets(username, buyTickets.quantity, buyTickets.zones)
        return tickets
    }



}