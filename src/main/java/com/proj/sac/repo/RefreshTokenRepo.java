package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.RefreshToken;
import java.util.List;
import java.time.LocalDateTime;


public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer>
{
	RefreshToken findByToken(String token);
	List<RefreshToken> findByExpirationBefore(LocalDateTime expiration);
}
