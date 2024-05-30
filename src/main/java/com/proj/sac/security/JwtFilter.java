package com.proj.sac.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proj.sac.entity.AccessToken;
import com.proj.sac.exception.AccessTokenNotFoundException;
import com.proj.sac.exception.UsernameNotFoundException;
import com.proj.sac.repo.AccessTokenRepo;
import com.proj.sac.securityfilters.FilterHelper;

import io.jsonwebtoken.ExpiredJwtException;
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
public class JwtFilter extends OncePerRequestFilter {
	private JwtService jwtService;
	private AccessTokenRepo accessTokenRepo;
	
//	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String at = null;
//		String rt = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("at"))
					at = cookie.getValue();
//				if (cookie.getName().equals("rt"))
//					rt = cookie.getValue();
			}

			String username = null;
			String userRole = null;

//			if (at != null && rt != null) {
			if (at != null) {
				List<AccessToken> accessToken = accessTokenRepo.findByTokenAndIsBlocked(at, false);

				if (accessToken == null)
					throw new AccessTokenNotFoundException("Failed to locate Access Token");
				else {
					log.info("Authenticating the Token");
					try {
						
						username = jwtService.extractUsername(at);
						userRole = jwtService.extractUserRole(at);
					} catch (ExpiredJwtException e) {
						log.info("Tokens are expired");
						FilterHelper.handleException(response, e.getMessage());
					}
					if (username == null)
						throw new UsernameNotFoundException("Username doesnt exist !!!");
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(new SimpleGrantedAuthority(userRole)));
					token.setDetails(new WebAuthenticationDetails(request));
					SecurityContextHolder.getContext().setAuthentication(token);
					log.info("Authenticated Successfully");
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
