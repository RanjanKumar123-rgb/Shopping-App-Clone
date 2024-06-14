package com.proj.sac.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactExceedException extends RuntimeException 
{
	private final String message;
}
