package it.polito.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
class ExceptionHandlerController: ResponseEntityExceptionHandler () {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmpty::class)
    fun handleUserEmpty(ex: UserEmpty,  req: WebRequest?){
            println("user, password and email cannot be empty")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotUnique::class)
    fun handleUserNotUnique(ex: UserNotUnique,  req: WebRequest?){
            println("user and email need to be unique")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserPasswordNotStrong::class)
    fun handleUserPasswordNotStrong(ex: UserPasswordNotStrong,  req: WebRequest?){

        println("password not strong (it must not contain any whitespace, it must be\n" +
                    "at least 8 characters long, it must contain at least one digit, one uppercase letter, one\n" +
                    "lowercase letter, one non alphanumeric character) ")

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailNotValid::class)
    fun handleEmailNotValid(ex: EmailNotValid,  req: WebRequest?){
            println("email not valid")
    }

}

class UserEmpty() : Exception()
class UserNotUnique() : Exception()
class UserPasswordNotStrong() : Exception()
class EmailNotValid() : Exception()
class ActivationIDNotFound() : Exception()
class ActivationCodeMismatch() : Exception()
class ActivationCodeExpired() : Exception()



