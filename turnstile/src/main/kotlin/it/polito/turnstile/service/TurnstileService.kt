package it.polito.turnstile.service

import it.polito.turnstile.dto.TransitDTO
import it.polito.turnstile.dto.TurnstileDetailsDTO
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TurnstileService{

    suspend fun checkTicket(qrcode: String, turnstileUsername: String): Boolean

    suspend fun getTransits(since: LocalDateTime?, to: LocalDateTime?, username: String?): Flow<TransitDTO>

    suspend fun getSecret(): String

    suspend fun addDetails(detailsDTO: TurnstileDetailsDTO)

    suspend fun getTurnstileDetails(turnstileUsername : String) :TurnstileDetailsDTO
}