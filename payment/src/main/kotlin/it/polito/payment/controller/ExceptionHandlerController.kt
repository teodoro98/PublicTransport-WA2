package it.polito.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
class ExceptionHandlerController{


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InternalServerErrorException::class)
    fun internalServerError (ex: InternalServerErrorException, req: WebRequest?){
        println("Internal server error in kafka")
    }

   

}


class InternalServerErrorException() : Exception()



