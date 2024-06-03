package com.proj.sac.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService 
{
	@Value("${myapp.secret}")
	private String secret;
	
	@Value("${myapp.access.expiry}")
	private Long accessExpireationInSecs;
	
	@Value("${myapp.refresh.expiry}")
	private Long refreshExpireationInSecs;
	
	public static final String CLAIM_ROLE = "role";
	
	public String generateAccessToken(String userRole, String username)
	{
		return generateJWT(userRole, username, accessExpireationInSecs * 1000l);
	}
	
	public String generateRefreshToken(String userRole, String username)
	{
		return generateJWT(userRole, username,  refreshExpireationInSecs * 1000l);
	}
	
	private String generateJWT(String role, String username, Long expiry)
	{
		return Jwts.builder()
				.setClaims(Map.of(CLAIM_ROLE, role))
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiry))
				.signWith(getSignature(), SignatureAlgorithm.HS512)
				.compact();
	}
	
	private Key getSignature()
	{
		byte[] secretBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(secretBytes);
	}
	
	private Claims jwtParser(String token)
	{
		JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSignature()).build();
		return jwtParser.parseClaimsJws(token).getBody();
	}
	
	public String extractUsername(String token)
	{
		return jwtParser(token).getSubject();
	}
	
	public String extractUserRole(String token)
	{
		return jwtParser(token).get(CLAIM_ROLE, String.class);
	}
}
