package com.proj.sac.util;

import org.springframework.stereotype.Component;

@Component
public class ErrorStructure<T> 
{
	private int status;
	private String message;
	private T rootCuase;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getRootCuase() {
		return rootCuase;
	}
	public void setRootCuase(T rootCuase) {
		this.rootCuase = rootCuase;
	}
}
