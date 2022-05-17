package it.polito.traveler.service

import it.polito.traveler.dto.UserDetailsDTO
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
    //TODO

    // userDetailsRepository.save(userDetailsDTO)
    
    }

/*
        val id = userDetailsDTO.id
        if (userDetailsRepository.existsById(id!!)){
            val originalUser = userDetailsRepository.findById(id).get()
            val updatedUser = UserDetails(
                id = originalUser.id,
                name = if (userDetailsDTO.name != "") userDetailsDTO.name else originalUser.name,
                address = if (userDetailsDTO.address != "") userDetailsDTO.address else originalUser.address,
                dateOfBirth = if (userDetailsDTO.dateOfBirth != "") userDetailsDTO.dateOfBirth else originalUser.dateOfBirth,
                telephoneNumber = if (userDetailsDTO.telephoneNumber != "") userDetailsDTO.telephoneNumber else originalUser.telephoneNumber
            )
        }
         */


    override fun getTickets() {
        TODO("Not yet implemented")
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