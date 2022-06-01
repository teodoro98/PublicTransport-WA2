package it.polito.payment.controller

import it.polito.payment.dto.TransactionDTO
import it.polito.payment.repository.TransactionRepository
import it.polito.payment.security.UserDetailsImpl
import it.polito.payment.service.PaymentService
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class PaymentController {

    private lateinit var paymentService: PaymentService

    @GetMapping("/admin/transactions")
    @ResponseStatus(HttpStatus.FOUND)
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun getTransactions(): Flow<TransactionDTO> {
        return paymentService.getTransactions()
    }

    @GetMapping("/transactions")
    @ResponseStatus(HttpStatus.FOUND)
    suspend fun getUserTransactions(principal: Principal): Flow<TransactionDTO> {
        val userDetails: UserDetailsImpl = (principal as UsernamePasswordAuthenticationToken).principal as UserDetailsImpl
        val userId: Long = userDetails.getId()
        return paymentService.getUserTransactions(userId)
    }
}