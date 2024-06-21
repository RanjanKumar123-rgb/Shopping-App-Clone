package com.proj.sac.security;

import com.proj.sac.repo.AccessTokenRepo;
import com.proj.sac.entity.AccessToken;
import com.proj.sac.exception.AccessTokenNotFoundException;
import com.proj.sac.exception.UsernameNotFoundException;
import com.proj.sac.securityfilters.FilterHelper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private AccessTokenRepo accessTokenRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("Authenticating the Token");

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Optional<Cookie> atCookie = findAccessTokenCookie(cookies);

            if (atCookie.isPresent()) {
                processAccessToken(request, response, atCookie.get().getValue());
            } else {
                log.warn("Cookie 'at' not found in the request");
            }
        } else {
            log.warn("No cookies found in the request");
        }

        filterChain.doFilter(request, response);
    }

    private Optional<Cookie> findAccessTokenCookie(Cookie[] cookies) {
        return Arrays.stream(cookies).filter(cookie -> "at".equals(cookie.getName())).findFirst();
    }

    private void processAccessToken(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {
        List<AccessToken> accessTokenList = accessTokenRepo.findByTokenAndIsBlocked(token, false);

        if (accessTokenList == null || accessTokenList.isEmpty()) {
            throw new AccessTokenNotFoundException("Failed to locate Access Token");
        }

        try {
            String username = jwtService.extractUsername(token);
            String userRole = jwtService.extractUserRole(token);
            authenticateUser(request, username, userRole);
            log.info("Authenticated Successfully");
        } catch (ExpiredJwtException e) {
            log.info("Tokens are expired");
            FilterHelper.handleException(response, e.getMessage());
        }
    }

    private void authenticateUser(HttpServletRequest request, String username, String userRole) {
        if (username == null) {
            throw new UsernameNotFoundException("Username doesn't exist !!!");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(new SimpleGrantedAuthority(userRole)));
        authenticationToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}