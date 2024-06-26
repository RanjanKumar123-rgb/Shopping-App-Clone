package com.proj.sac.repo;

import com.proj.sac.entity.AccessToken;
import com.proj.sac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Integer>
{
	AccessToken findByToken(String token);
	List<AccessToken> findByTokenAndIsBlocked(String at, boolean isBlocked);
	List<AccessToken> findByExpirationBefore(LocalDateTime expiration);
	List<AccessToken> findByUserAndIsBlocked(User user, boolean b);
	List<AccessToken> findByUserAndIsBlockedAndTokenNot(User user, boolean b, String token);
	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String accessToken);
}
