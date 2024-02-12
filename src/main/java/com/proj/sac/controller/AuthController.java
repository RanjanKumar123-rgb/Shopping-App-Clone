package com.proj.sac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OTPmodel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.AuthResponse;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletResponse;

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

	@PostMapping(path = "/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OTPmodel OTP)
	{
		return service.verifyOTP(OTP);
	}
	
	@PostMapping(path = "/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest, HttpServletResponse response)
	{
		return service.login(authRequest, response);
	}
	
	@PostMapping(path = "/logout")
	public ResponseEntity<SimpleResponseStructure<AuthResponse>> logout(@CookieValue(name = "rt", required = false) String refreshToken,@CookieValue(name = "at", required = false) String accessToken ,HttpServletResponse response)
	{
		return service.logout(refreshToken, accessToken, response);
	}
}
