package com.proj.sac.service;

import com.proj.sac.entity.User;
import org.springframework.http.ResponseEntity;

import com.proj.sac.util.ResponseStructure;

public interface UserService {

	ResponseEntity<ResponseStructure<User>> findUserByUserId(int userId);

}
