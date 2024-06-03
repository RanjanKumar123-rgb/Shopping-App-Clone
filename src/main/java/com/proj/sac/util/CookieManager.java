package com.proj.sac.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieManager 
{
	@Value("${myapp.domain}")
	private String domain;
	
	public Cookie configureCookie(Cookie cookie, int expirationInSecs)
	{
		cookie.setDomain(domain);
		cookie.setSecure(false);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expirationInSecs);
		cookie.setPath("/");
		
		return cookie;
	}

	public Cookie invalidate(Cookie cookie) 
	{
		cookie.setMaxAge(0);
        cookie.setPath("/"); 
        return cookie;
	}
}
