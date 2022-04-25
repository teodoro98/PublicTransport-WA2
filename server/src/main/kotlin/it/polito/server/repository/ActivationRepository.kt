package it.polito.server.repository

import it.polito.server.dto.UserDTO
import it.polito.server.entity.Activation
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ActivationRepository:CrudRepository<Activation, UUID?> {
}