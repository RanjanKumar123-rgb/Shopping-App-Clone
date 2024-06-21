package com.proj.sac.repo;

import java.util.Optional;

import com.proj.sac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends JpaRepository<User, Integer>
{
	boolean existsByEmail(String email);
	Optional<User> findByUsername(String username);
	
}
