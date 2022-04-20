package it.polito.server.repository

import it.polito.server.entity.Activation
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ActivationRepository:CrudRepository<Activation, UUID?> {
}