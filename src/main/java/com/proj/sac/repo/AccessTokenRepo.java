package com.proj.sac.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.AccessToken;
import com.proj.sac.entity.User;



public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer>
{
	AccessToken findByToken(String token);
	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean isBlocked);
	List<AccessToken> findByExpirationBefore(LocalDateTime expiration);
	Optional<AccessToken> findByUserAndIsBlocked(User user, boolean b);
	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String token);
}
