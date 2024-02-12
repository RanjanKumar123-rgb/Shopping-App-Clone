package com.proj.sac.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proj.sac.entity.User;


@Repository
public interface UserRepo extends JpaRepository<User, Integer>
{
	boolean existsByEmail(String email);
	Optional<User> findByUsername(String username);
	
}
