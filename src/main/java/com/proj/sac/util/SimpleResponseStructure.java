package com.proj.sac.util;

import org.springframework.stereotype.Component;

@Component
public class SimpleResponseStructure
{
	private int statusCode;
	private String message;
	
	public int getStatusCode() {
		return statusCode;
	}
	public SimpleResponseStructure setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	public String getMessage() {
		return message;
	}
	public SimpleResponseStructure setMessage(String message) {
		this.message = message;
		return this;
	}
}
