package com.springBoot.olympischeSpelen;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import exception.EntityNotFound;


@RestControllerAdvice
public class RestExceptionHandler {
	
	@Autowired
	private MessageSource messageSource;
	
	@ResponseBody
    @ExceptionHandler(EntityNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleWedstrijdNotFoundException(EntityNotFound ex, Locale locale) {
        return  messageSource.getMessage(ex.getMessage(), new Object[] {}, locale);
    }

   
}