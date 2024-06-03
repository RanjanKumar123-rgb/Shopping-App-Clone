package com.proj.sac.requestdto;

import com.proj.sac.enums.UserRole;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserRequest 
{
	@Column(unique = true)
	private String email;
	
	private String password;
	
	private UserRole userRole;
}
