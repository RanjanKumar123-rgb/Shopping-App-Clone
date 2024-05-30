package com.proj.sac.exception;

import lombok.Getter;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UsernameNotFoundException extends RuntimeException
{
	private String message;
}