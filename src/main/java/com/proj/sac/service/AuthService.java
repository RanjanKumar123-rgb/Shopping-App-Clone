package com.proj.sac.service;

import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OtpModel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.AuthResponse;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService 
{
	ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(String refreshToken, String accessToken, AuthRequest authRequest, HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure> logout(String refreshToken, String accessToken, HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure> revokeAllDevices();

	ResponseEntity<SimpleResponseStructure> revokeOtherDevices(String accessToken, String refreshToken, HttpServletResponse response);

	ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String accessToken, String refreshToken, HttpServletResponse response);
}
