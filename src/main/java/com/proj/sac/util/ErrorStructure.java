package com.proj.sac.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class ErrorStructure<T> 
{
	private int status;
	private String message;
	private T rootCause;

}
