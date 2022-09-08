package it.polito.ticketcatalogue.controller;

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
    @ExceptionHandler(UserEmpty::class)
    fun handleUserEmpty(ex: UserEmpty){
        println("user, password and email cannot be empty")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InternalServerErrorException::class)
    fun internalServerError (ex: InternalServerErrorException){
        println("Internal server error in kafka")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoTicketFoundException::class)
    fun noTicketFound (ex: NoTicketFoundException){
        println("No ticket found")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OrderNotFoundException::class)
    fun orderNotFound (ex: OrderNotFoundException){
        println("No order found")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TicketNotCompatibleException::class)
    fun ticketNotCompatible (ex: TicketNotCompatibleException){
        println("Ticket not compatible")
    }

}

class UserEmpty() : Exception()
class InternalServerErrorException() : Exception()
class NoTicketFoundException() : Exception()
class OrderNotFoundException(): Exception()
class TicketNotCompatibleException(): Exception()



