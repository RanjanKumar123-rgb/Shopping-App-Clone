package com.proj.sac.controller;

import com.proj.sac.entity.User;
import com.proj.sac.service.UserService;
import com.proj.sac.util.ResponseStructure;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.base_url}")
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
