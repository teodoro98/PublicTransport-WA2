package it.polito.traveler.service

import it.polito.server.controller.UserEmpty
import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.TicketPurchased
import it.polito.traveler.entity.UserDetails
import it.polito.traveler.repository.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TravelerServiceImpl(@Value("\${server.ticket.token.secret}")clearSecret: String):TravelerService {

    // Repositories
    @Autowired
    private lateinit var userDetailsRepository :  UserDetailsRepository
    @Autowired
    private lateinit var ticketPurchasedRepository: UserDetailsRepository


    override fun getProfile(id:Long): UserDetailsDTO {
        return userDetailsRepository.findById(id).get().toDTOUserDetails()
    }

    override fun updateProfile(userDetailsDTO: UserDetailsDTO) {
        var user: UserDetails
        try {
            user = userDetailsRepository.findById(userDetailsDTO.id!!).get()
        }catch (e : Exception){
            throw UserEmpty()
        }
        user.address= userDetailsDTO.address
        user.name= userDetailsDTO.name
        user.telephoneNumber= userDetailsDTO.telephoneNumber
        user.dateOfBirth = userDetailsDTO.dateOfBirth
        userDetailsRepository.save(user)
    
    }

    override fun getTickets(id: Long) : List<TicketPurchasedDTO> {
        val listTickets = mutableListOf<TicketPurchasedDTO>()
        val tickets = userDetailsRepository.findTicketsById(id)
        for (t in tickets!!){
            listTickets.add(t)
        }
        return listTickets
    }

    override fun buyTickets(id: Long, quantity: Int, zones: String) {
        val purchasedTickets = mutableListOf<TicketPurchasedDTO>()
        val user: UserDetails
        try {
            user = userDetailsRepository.findById(id).get()
        }catch (e : Exception){
            throw UserEmpty()
        }
        repeat(quantity){
            val purchasedTicket = TicketPurchased(
                user,
                java.sql.Timestamp(System.currentTimeMillis()),

            )
        }
    }

    override fun getTravelers() : List<UserDetailsDTO>{
        val usersList = mutableListOf<UserDetailsDTO>()
        val users = userDetailsRepository.findAll()
        for (u in users!!){
            usersList.add(u.toDTOUserDetails())
        }
        return usersList
    }

    // endpoints relative to GET /admin/traveler/{userID}/tickets and GET /admin/traveler/{userID}/profile use the
    // service functions getProfile and getTickets


}