package com.proj.sac.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proj.sac.entity.AccessToken;
import com.proj.sac.entity.RefreshToken;
import com.proj.sac.repo.AccessTokenRepo;
import com.proj.sac.repo.RefreshTokenRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ScheduledJobs 
{
	private RefreshTokenRepo refreshTokenRepo;
	private AccessTokenRepo accessTokenRepo;
	
	@Scheduled(cron = "0 */6 * * *")
	public void removeExpiredRefreshToken() {

		List<RefreshToken> refreshTokenList = refreshTokenRepo.findByExpirationBefore(LocalDateTime.now());
		refreshTokenRepo.deleteAll(refreshTokenList);
	}

	@Scheduled(cron = "0 */6 * * *")
	public void removeExpiredAccessToken() {

		List<AccessToken> accessTokenList = accessTokenRepo.findByExpirationBefore(LocalDateTime.now());
		accessTokenRepo.deleteAll(accessTokenList);
	}
}
