package it.polito.traveler.service

import it.polito.traveler.dto.UserDetailsDTO
import it.polito.traveler.entity.UserDetails
import it.polito.traveler.repository.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

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

    override fun buyTickets() {
        TODO("Not yet implemented")
    }

    override fun getTravelers() {
        TODO("Not yet implemented")
    }

    override fun findTraveler() {
        TODO("Not yet implemented")
    }

    override fun getTicketsOfTraveler() {
        TODO("Not yet implemented")
    }

}