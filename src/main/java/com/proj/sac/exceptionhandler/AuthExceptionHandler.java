package com.proj.sac.exceptionhandler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.proj.sac.exception.UserAlreadyExistEception;

@RestControllerAdvice
public class AuthExceptionHandler 
{
	private static ResponseEntity<Object> structure(HttpStatus status, String message, Object rootcause)
	{
		return new ResponseEntity<Object>(Map.of(
				"status",status.value(),
				"message",message,
				"rootcause",rootcause
					),status);
	}
	
	@ExceptionHandler(UserAlreadyExistEception.class)
	public ResponseEntity<Object> handleUserAlreadyExistEception(UserAlreadyExistEception ex)
	{
		return structure(HttpStatus.UNAUTHORIZED, ex.getMessage(), "User ID already exist. Try a new user id !!!");
	}
}
