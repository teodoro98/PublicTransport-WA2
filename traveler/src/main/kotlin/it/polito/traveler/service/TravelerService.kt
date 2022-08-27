package it.polito.traveler.service

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.TicketPurchased

interface TravelerService {

    fun getProfile( username:String ): UserDetailsDTO

    fun updateProfile(userDetailsDTO: UserDetailsDTO, username: String)

    fun getTickets(username : String) : List<TicketPurchasedDTO>

    fun buyTickets(username: String, quantity: Int, zones: String): MutableList<TicketPurchasedDTO>

    fun getTravelers() : List<UserDetailsDTO>


}