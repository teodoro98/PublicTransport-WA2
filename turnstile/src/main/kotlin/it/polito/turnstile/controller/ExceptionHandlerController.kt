package it.polito.turnstile.controller;

import it.polito.turnstile.security.JwtUtils
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
class ExceptionHandlerController{

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TicketNotValidException::class)
    fun ticketNotValid(ex: TicketNotValidException){
        println("Ticket not valid")
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(TypeNotFoundException::class)
    fun typeNotFound (ex: TypeNotFoundException){
        println("Type not found")
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TurnstileNotFoundException::class)
    fun turnstileNotFound (ex: TurnstileNotFoundException){
        println("Turnstile not found")
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(ValidityTimeExpiredException::class)
    fun validityTimeExpired (ex: ValidityTimeExpiredException){
        println("Validity time expired")
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MaxRidesReachedException::class)
    fun maxRidesReached (ex: MaxRidesReachedException){
        println("Max rides reached")
    }
}

class TicketNotValidException(): Exception()
class TypeNotFoundException(): Exception()
class TurnstileNotFoundException(): Exception()
class ValidityTimeExpiredException(): Exception()
class MaxRidesReachedException(): Exception()




