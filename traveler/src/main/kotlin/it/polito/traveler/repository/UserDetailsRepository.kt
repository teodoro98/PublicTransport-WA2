package it.polito.traveler.repository

import it.polito.traveler.entity.UserDetails
import org.springframework.data.repository.CrudRepository

interface UserDetailsRepository: CrudRepository<UserDetails,Long> {
}