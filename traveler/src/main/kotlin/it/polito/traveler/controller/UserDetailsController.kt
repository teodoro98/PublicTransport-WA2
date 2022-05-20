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


    //TODO dubbio: da dove si prende l'ID? Immaginiamo dal login ma non sappiamo poi come
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.FOUND)
    fun getProfile(id:Long): UserDetailsDTO{
        //id from login security
        val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        return travelerService.getProfile(userDetails.username)
    }

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun updateProfile(user : UserDetailsDTO){
        /*val userDetails: UserDetailsImpl =
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() as UserDetailsImpl
        val id = userDetails.getId()*/
        travelerService.updateProfile(user/*, id*/)
    }

    @GetMapping("/tickets")
    @ResponseStatus(HttpStatus.FOUND)
    fun getTickets(id:Long): List<TicketPurchasedDTO>{
        return travelerService.getTickets(id)
    }

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun buyTickets(id: Long, quantity: Int, zones: String){
        travelerService.buyTickets(id, quantity, zones)
    }



}