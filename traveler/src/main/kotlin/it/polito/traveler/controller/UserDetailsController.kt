package it.polito.traveler.controller

import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.service.TravelerServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/my")
class UserDetailsController {


    @Autowired
    private lateinit var travelerService: TravelerServiceImpl


    //TODO dubbio: da dove si prende l'ID? Immaginiamo dal login ma non sappiamo poi come
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.FOUND)
    fun getProfile(id:Long): UserDetailsDTO{
        return travelerService.getProfile(id)
    }

}