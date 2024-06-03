package com.proj.sac.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
@AllArgsConstructor
public class StoreNotFoundException extends RuntimeException 
{
	private final String message;
}
