package com.proj.sac.exceptionhandler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.sac.exception.AccessTokenNotFoundException;
import com.proj.sac.exception.UserAlreadyExistException;
import com.proj.sac.util.ErrorStructure;

@RestControllerAdvice
public class AuthExceptionHandler 
{
    private final ObjectMapper objectMapper;

    public AuthExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
	
	private static ResponseEntity<Object> structure(HttpStatus status, String message, Object rootCause)
	{
		return new ResponseEntity<>(Map.of(
				"status",status.value(),
				"message",message,
				"rootCause",rootCause
					),status);
	}
	
	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<Object> handleUserAlreadyExistException(UserAlreadyExistException ex)
	{
		return structure(HttpStatus.UNAUTHORIZED, ex.getMessage(), "User ID already exist. Try a new user id !!!");
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex)
	{
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "Failed to authenticate the User or Username not found !!!");
	}
	
	@ExceptionHandler(AccessTokenNotFoundException.class)
    public ResponseEntity<Object> handleAccessTokenNotFoundException(AccessTokenNotFoundException ex) {
        ErrorStructure<Object> errorStructure = new ErrorStructure<>();
        
        errorStructure.setMessage(ex.getMessage());
        errorStructure.setStatus(HttpStatus.NOT_FOUND.value());
        errorStructure.setRootCuase("Failed to locate Access Token or Access Token Expired !!!");
        
        try {
            String json = objectMapper.writeValueAsString(errorStructure);
            return new ResponseEntity<>(json, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"message\":\"Error processing error response\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
}
