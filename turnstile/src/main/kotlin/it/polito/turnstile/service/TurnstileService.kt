package it.polito.turnstile.service

import it.polito.turnstile.dto.TransitDTO
import kotlinx.coroutines.flow.Flow

interface TurnstileService{

    suspend fun checkTicket(qrcode: String, turnstileUsername: String): Boolean

    suspend fun getTransits(since: LocalDateTime?, to: LocalDateTime?, username: String?): Flow<TransitDTO>

    suspend fun getSecret(): String
}