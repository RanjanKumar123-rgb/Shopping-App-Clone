package com.proj.sac.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    public static final String CLAIM_ROLE = "role";
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    @Value("${app.jwt.secret}")
    private String secret;
    @Value("${app.jwt.token.access_expiry_seconds}")
    private Long accessExpirationInSecs;
    @Value("${app.jwt.token.refresh_expiry_seconds}")
    private Long refreshExpirationInSecs;

    public String generateAccessToken(String userRole, String username) {
        log.info("Generating Access Token");
        return generateJWT(userRole, username, accessExpirationInSecs * 1000L);
    }

    public String generateRefreshToken(String userRole, String username) {
        log.info("Generating Refresh Token");
        return generateJWT(userRole, username, refreshExpirationInSecs * 1000L);
    }

    private String generateJWT(String role, String username, Long expiry) {
        log.info("Generating JWT");
        return Jwts.builder().setClaims(Map.of(CLAIM_ROLE, role)).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expiry)).signWith(getSignature(), SignatureAlgorithm.HS512).compact();
    }

    private Key getSignature() {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    private Claims jwtParser(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSignature()).build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return jwtParser(token).getSubject();
    }

    public String extractUserRole(String token) {
        return jwtParser(token).get(CLAIM_ROLE, String.class);
    }
}
