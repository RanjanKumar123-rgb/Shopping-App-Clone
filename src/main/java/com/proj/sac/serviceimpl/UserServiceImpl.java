package com.proj.sac.serviceimpl;

import com.proj.sac.entity.User;
import com.proj.sac.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proj.sac.repo.UserRepo;
import com.proj.sac.service.UserService;
import com.proj.sac.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService
{
	private UserRepo userRepo;
	ResponseStructure<User> userStructure;
	
	@Override
	public ResponseEntity<ResponseStructure<User>> findUserByUserId(int userId) 
	{
		return userRepo.findById(userId).map(user -> {
			userStructure.setData(user);
			userStructure.setMessage("User Data Fetched");
			userStructure.setStatusCode(HttpStatus.FOUND.value());
			
			return new ResponseEntity<>(userStructure, HttpStatus.OK);
		}).orElseThrow(()-> new UserNotFoundException("Failed to find the user !!!"));
	}
}
