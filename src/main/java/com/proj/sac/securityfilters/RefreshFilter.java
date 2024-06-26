package com.proj.sac.securityfilters;

import com.proj.sac.repo.RefreshTokenRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proj.sac.exception.UserNotLoggedInException;
import com.proj.sac.security.JwtService;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class RefreshFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private RefreshTokenRepo refreshTokenRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Authenticating Token with Refresh Filter...");
        String rt = FilterHelper.extractCookie("rt", request.getCookies());

        if (rt == null) throw new UserNotLoggedInException("User not logged in | no credentials found");
        if (refreshTokenRepo.existsByTokenAndIsBlocked(rt, true))
            throw new UserNotLoggedInException("Access blocked | try login again");

        try {
            log.info("Extracting credentials...");
            String username = jwtService.extractUsername(rt);
            String roles = jwtService.extractUserRole(rt);

            FilterHelper.setAuthentication(username, roles, request);
            log.info("JWT Authentication Successful");

        } catch (ExpiredJwtException ex) {
            FilterHelper.handleException(response, "Your refreshToken is expired, try login again");
        } catch (JwtException ex) {
            FilterHelper.handleException(response, "Authentication Failed | " + ex.getMessage());
        } catch (UserNotLoggedInException ex) {
            log.info("Authentication failed | User already logged in");
            FilterHelper.handleException(response, ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}