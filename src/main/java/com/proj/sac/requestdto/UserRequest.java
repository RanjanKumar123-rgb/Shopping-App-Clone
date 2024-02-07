package com.proj.sac.requestdto;

import com.proj.sac.enums.UserRole;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserRequest 
{
	@Column(unique = true)
	private String email;
	
	private String password;
	
	private UserRole userRole;
}
