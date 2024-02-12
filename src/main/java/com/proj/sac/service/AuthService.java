package com.proj.sac.service;

import org.springframework.http.ResponseEntity;

import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OTPmodel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.AuthResponse;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService 
{
	ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OTPmodel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest, HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure<AuthResponse>> logout(String refreshToken, String accessToken, HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure<AuthResponse>> revokeAllDevices();
}
