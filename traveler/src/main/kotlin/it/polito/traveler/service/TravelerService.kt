package it.polito.traveler.service

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.TicketPurchased

interface TravelerService {

    fun getProfile( username:String ): UserDetailsDTO

    fun updateProfile(userDetailsDTO: UserDetailsDTO, username: String)

    fun getTickets(id : Long) : List<TicketPurchasedDTO>

    fun buyTickets(id: Long, quantity: Int, zones: String)

    fun getTravelers() : List<UserDetailsDTO>


}