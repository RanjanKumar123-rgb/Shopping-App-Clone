package com.proj.sac.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.sac.entity.User;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.UserService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class UserController 
{
	private UserService service;
	
	@GetMapping(path = "/users/{userId}")
	public ResponseEntity<ResponseStructure<User>> findUserByUserId(@PathVariable int userId)
	{
		return service.findUserByUserId(userId);
	}
}
