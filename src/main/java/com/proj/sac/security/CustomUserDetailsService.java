package com.proj.sac.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proj.sac.entity.User;
import com.proj.sac.repo.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		
		User user = userRepo.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username doesnt exists !!!"));
		return new CustomUserDetails(user);
	}

}
