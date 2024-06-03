package com.proj.sac.security;

import com.proj.sac.entity.User;
import com.proj.sac.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{
		User user = userRepo.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username doesnt exists !!!"));
		return new CustomUserDetails(user);
	}
}
