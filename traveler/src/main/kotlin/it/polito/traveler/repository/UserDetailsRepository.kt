package it.polito.traveler.repository

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.entity.TicketPurchased
import it.polito.traveler.entity.UserDetails
import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface UserDetailsRepository: CrudRepository<UserDetails,Long> {

    @Transactional
    @Query("select u.ticketPurchased from UserDetails u where u.id<=:id")
    fun findTicketsById(@Param("id")id : Long): List<TicketPurchasedDTO>?

    @Transactional
    @Query("select u.id from UserDetails u where u.username=:username")
    fun findIdByUsername(@Param("username")username : String): Long?


}