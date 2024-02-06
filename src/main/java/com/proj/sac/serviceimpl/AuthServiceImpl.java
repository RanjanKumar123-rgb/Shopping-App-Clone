package com.proj.sac.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.proj.sac.repo.UserRepo;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.ResponseStructure;


public class AuthServiceImpl implements AuthService
{
	@Autowired
	UserRepo userRepo;
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> requestUser(UserRequest userRequest) 
	{
		
	}

}
