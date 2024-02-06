package com.proj.sac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.ResponseStructure;

public class AuthController 
{
	@Autowired
	AuthService service;
	
	@PostMapping(path = "/users")
	public ResponseEntity<ResponseStructure<UserResponse>> requestUser(@RequestBody UserRequest userRequest)
	{
		return service.requestUser(userRequest);
	}
}
