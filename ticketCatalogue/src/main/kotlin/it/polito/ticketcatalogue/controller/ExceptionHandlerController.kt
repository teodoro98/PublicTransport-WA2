package it.polito.ticketcatalogue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
class ExceptionHandlerController{

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmpty::class)
    fun handleUserEmpty(ex: UserEmpty, req: WebRequest?){
        println("user, password and email cannot be empty")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InternalServerErrorException::class)
    fun internalServerError (ex: InternalServerErrorException, req: WebRequest?){
        println("Internal server error in kafka")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoTicketFoundException::class)
    fun noTicketFound (ex: NoTicketFoundException, req: WebRequest?){
        println("No ticket found")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OrderNotFoundException::class)
    fun orderNotFound (ex: OrderNotFoundException, req: WebRequest?){
        println("No order found")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TicketNotCompatibleException::class)
    fun ticketNotCompatible (ex: TicketNotCompatibleException, req: WebRequest?){
        println("Ticket not compatible")
    }

}

class UserEmpty() : Exception()
class InternalServerErrorException() : Exception()
class NoTicketFoundException() : Exception()
class OrderNotFoundException(): Exception()
class TicketNotCompatibleException(): Exception()



