package com.proj.sac.security;

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

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private AccessTokenRepo accessTokenRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String at = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("at"))
                .map(Cookie::getValue).toList().getFirst();

        String username = null;
        String userRole = null;

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
                    throw new UsernameNotFoundException("Username doesn't exist !!!");
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(new SimpleGrantedAuthority(userRole)));
                token.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
                log.info("Authenticated Successfully");
            }
        }
        filterChain.doFilter(request,response);
    }
}