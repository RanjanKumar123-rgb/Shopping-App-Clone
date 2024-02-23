package com.proj.sac.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proj.sac.entity.AccessToken;
import com.proj.sac.repo.AccessTokenRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter
{
	private AccessTokenRepo accessTokenRepo;
	private JwtService jwtService;
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
	{
		String at=null;
		String rt=null;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null)
		{
			for(Cookie cookie:cookies)
			{
				if(cookie.getName().equals("at"))
						at=cookie.getValue();
				if(cookie.getName().equals("rt"))
					rt=cookie.getValue();
			}
			
			String username=null;
			
			if(at!=null && rt!=null)
			{
				List<AccessToken> accessToken = accessTokenRepo.findByTokenAndIsBlocked(at, false);
				
				if(accessToken == null)
					throw new RuntimeException();
				else{
					log.info("Authenticating the Token");
					username = jwtService.extractUsername(at);
					if(username == null)
						throw new RuntimeException("Failed to authenticate");
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
					token.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(token);
					log.info("Authenticated Successfully");
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
