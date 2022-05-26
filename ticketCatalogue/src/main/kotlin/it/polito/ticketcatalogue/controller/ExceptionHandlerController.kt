package it.polito.ticketcatalogue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
class ExceptionHandlerController: ResponseEntityExceptionHandler () {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmpty::class)
    fun handleUserEmpty(ex: UserEmpty, req: WebRequest?){
        println("user, password and email cannot be empty")
    }

}

class UserEmpty() : Exception()



