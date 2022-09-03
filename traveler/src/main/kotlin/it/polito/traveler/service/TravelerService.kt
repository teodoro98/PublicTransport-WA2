package it.polito.traveler.service

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.TicketPurchased
import java.sql.Timestamp
import org.springframework.core.io.ByteArrayResource


interface TravelerService {

    fun getProfile( username:String ): UserDetailsDTO

    fun createProfile(userDetailsDTO: UserDetailsDTO, username : String)

    fun updateProfile(userDetailsDTO: UserDetailsDTO, username: String)

    fun getTickets(username : String) : List<TicketPurchasedDTO>

    fun getTicket(ticketId : Long) : TicketPurchasedDTO

    fun getQr(ticketId: Long, username: String): ByteArrayResource

    fun buyTickets(result: Boolean, username: String, quantity: Int, zones: String, type:String, validitytime: Timestamp?, maxnumberOfRides : Int?)

    fun getTravelers() : List<UserDetailsDTO>


}