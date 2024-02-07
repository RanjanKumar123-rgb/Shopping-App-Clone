package com.proj.sac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.ResponseStructure;

@RestController
@RequestMapping("/api/v1")
public class AuthController 
{
	@Autowired
	AuthService service;
	
	@PostMapping(path = "/register")
	public ResponseEntity<ResponseStructure<UserResponse>> requestUser(@RequestBody UserRequest userRequest)
	{
		return service.register(userRequest);
	}
}
