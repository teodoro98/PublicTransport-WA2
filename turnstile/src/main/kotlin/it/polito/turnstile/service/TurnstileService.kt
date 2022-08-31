package it.polito.turnstile.service

import it.polito.turnstile.dto.TransitDTO
import kotlinx.coroutines.flow.Flow

interface TurnstileService{

    suspend fun checkTicket(qrcode: String): Boolean

    suspend fun getTransits(): Flow<TransitDTO>

    suspend fun getSecret(): String
}