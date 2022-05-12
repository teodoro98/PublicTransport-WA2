package it.polito.traveler.repository

import it.polito.traveler.entity.UserDetails
import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.query.Param

interface UserDetailsRepository: CrudRepository<UserDetails,Long> {
    @Query("select ticketPurchased from UserDetails u where u.id<=:id")
    fun findTicketsById(@Param("id")id : Long): List<TicketPurchased>?
}