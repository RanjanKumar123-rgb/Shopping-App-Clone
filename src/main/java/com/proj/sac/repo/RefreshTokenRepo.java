package com.proj.sac.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.RefreshToken;
import com.proj.sac.entity.User;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer>
{
	RefreshToken findByToken(String token);
	List<RefreshToken> findByExpirationBefore(LocalDateTime expiration);
	Optional<RefreshToken> findByTokenAndIsBlocked(User user, boolean b);
}
