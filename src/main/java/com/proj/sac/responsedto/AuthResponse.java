package com.proj.sac.responsedto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse 
{
	private int userId;
	private String username;
	private boolean isAuthenticated;
	private String role;
	private LocalDateTime accessExpiration;
	private LocalDateTime refreshExpiration;
}