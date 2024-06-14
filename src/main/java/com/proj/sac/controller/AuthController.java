package com.proj.sac.controller;

import com.proj.sac.requestdto.AuthRequest;
import com.proj.sac.requestdto.OtpModel;
import com.proj.sac.requestdto.UserRequest;
import com.proj.sac.responsedto.AuthResponse;
import com.proj.sac.responsedto.UserResponse;
import com.proj.sac.service.AuthService;
import com.proj.sac.util.ResponseStructure;
import com.proj.sac.util.SimpleResponseStructure;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.base_url}")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class AuthController 
{
	AuthService service;
	
	@PostMapping(path = "/register")
	public ResponseEntity<ResponseStructure<UserResponse>> requestUser(@RequestBody UserRequest userRequest)
	{
		return service.register(userRequest);
	}

	@PostMapping(path = "/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OtpModel otp)
	{
		return service.verifyOTP(otp);
	}
	
	@PostMapping(path = "/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@CookieValue(name = "rt", required = false) String refreshToken,@CookieValue(name = "at", required = false) String accessToken, @RequestBody AuthRequest authRequest, HttpServletResponse response)
	{
		return service.login(refreshToken, accessToken, authRequest, response);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER') or hasAuthority('CUSTOMER')")
	@PostMapping(path = "/logout")
	public ResponseEntity<SimpleResponseStructure> logout(@CookieValue(name = "rt", required = false) String refreshToken,@CookieValue(name = "at", required = false) String accessToken ,HttpServletResponse response)
	{
		return service.logout(refreshToken, accessToken, response);
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER') or hasAuthority('CUSTOMER')")
	@PostMapping(path = "/revoke-all")
	public ResponseEntity<SimpleResponseStructure> revokeAllDevices()
	{
		return service.revokeAllDevices();
	}
	
	@PreAuthorize(value = "hasAuthority('SELLER') or hasAuthority('CUSTOMER')")
	@PostMapping(path = "/revoke-others")
	public ResponseEntity<SimpleResponseStructure> revokeOtherDevices(@CookieValue(name = "rt",required = false) String refreshToken, @CookieValue(name = "at",required = false) String accessToken, HttpServletResponse response)
	{
		return service.revokeOtherDevices(accessToken, refreshToken, response);
	}
	
	@PostMapping(path = "/refresh")
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(@CookieValue(name = "rt",required = false) String refreshToken, @CookieValue(name = "at",required = false) String accessToken, HttpServletResponse response)
	{
		return service.refreshLogin(accessToken, refreshToken, response);
	}
}
