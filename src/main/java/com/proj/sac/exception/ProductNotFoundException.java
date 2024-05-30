package com.proj.sac.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductNotFoundException extends RuntimeException 
{
	private String message;
}
