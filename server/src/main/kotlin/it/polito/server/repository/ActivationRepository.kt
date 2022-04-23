package it.polito.server.repository

import it.polito.server.entity.Activation
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ActivationRepository:CrudRepository<Activation, UUID?> {
}