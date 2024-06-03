package com.proj.sac.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj.sac.entity.RefreshToken;
import com.proj.sac.entity.User;


public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer>
{
	RefreshToken findByToken(String token);
	List<RefreshToken> findByExpirationBefore(LocalDateTime expiration);
	Optional<RefreshToken> findByTokenAndIsBlocked(User user, boolean b);
	List<RefreshToken> findByUserAndIsBlockedAndTokenNot(User user, boolean b, String refreshToken);
	List<RefreshToken> findByUserAndIsBlocked(User user, boolean b);
	boolean existsByTokenAndIsBlocked(String rt, boolean b);
}
