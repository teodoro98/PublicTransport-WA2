package it.polito.traveler.service

import it.polito.traveler.dto.UserDetailsDTO

interface TravelerService {

    fun getProfile( id:Long ): UserDetailsDTO

    fun updateProfile(userDetailsDTO: UserDetailsDTO)

    fun getTickets()

    fun buyTickets()

    fun getTravelers()

    fun findTraveler()

    fun getTicketsOfTraveler()


}