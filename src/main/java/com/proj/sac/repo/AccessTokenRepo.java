package com.proj.sac.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.AccessToken;
import com.proj.sac.entity.RefreshToken;

import java.time.LocalDateTime;



public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer>
{
	AccessToken findByToken(String token);
	Optional<AccessToken> findByTokenAndIsBlocked(String token, boolean isBlocked);
	List<AccessToken> findByExpirationBefore(LocalDateTime expiration);
}
