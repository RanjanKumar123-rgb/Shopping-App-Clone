package com.proj.sac.cache;

import com.proj.sac.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheBeanConfig 
{
	@Bean
	public CacheStore<User> userCacheStore()
	{
		return new CacheStore<>(Duration.ofMinutes(5));
	}
	
	@Bean
	public CacheStore<String> otpCacheStore()
	{
		return new CacheStore<>(Duration.ofMinutes(5));
	}
}