package it.polito.traveler.repository

import it.polito.traveler.dto.TicketPurchasedDTO
import it.polito.traveler.entity.TicketPurchased
import it.polito.traveler.entity.UserDetails
import org.apache.catalina.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface UserDetailsRepository: CrudRepository<UserDetails,Long> {

    @Transactional
    @Query("from TicketPurchased u where u.buyer=:user")
    fun findTicketsByUser(@Param("user")user : UserDetails): List<TicketPurchased>?

    @Transactional
    @Query("select u.id from UserDetails u where u.username=:username")
    fun findIdByUsername(@Param("username")username : String): Long?


}