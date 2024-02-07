package com.proj.sac.service;

import org.springframework.http.ResponseEntity;

import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.util.ResponseStructure;

public interface AuthService 
{
	ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest);
}
