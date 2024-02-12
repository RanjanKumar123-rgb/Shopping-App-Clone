package com.proj.sac.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proj.sac.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduledJobs 
{
	@Autowired
	private AuthService service;
	
	public void deleteExpiredTokens()
	{
		service.deleteExpiredTokens();
	}
}
