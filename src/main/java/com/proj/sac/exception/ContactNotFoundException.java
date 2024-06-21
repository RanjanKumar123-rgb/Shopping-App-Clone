package com.proj.sac.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactNotFoundException extends RuntimeException 
{
	private final String message;
}
