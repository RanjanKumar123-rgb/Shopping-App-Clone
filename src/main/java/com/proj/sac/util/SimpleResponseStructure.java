package com.proj.sac.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SimpleResponseStructure
{
	private int statusCode;
	private String message;

    public SimpleResponseStructure setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

    public SimpleResponseStructure setMessage(String message) {
		this.message = message;
		return this;
	}
}
