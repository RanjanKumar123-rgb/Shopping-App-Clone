package com.proj.sac.securityfilters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.sac.util.SimpleResponseStructure;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterHelper {
	public static void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("Application/json");
        response.setHeader("error", message);
        SimpleResponseStructure structure = new SimpleResponseStructure()
                .setStatusCode(HttpStatus.UNAUTHORIZED.value())
                .setMessage(message);
        new ObjectMapper().writeValue(response.getOutputStream(), structure);
    }

	public static String extractCookie(String cookieName, Cookie[] cookies) {
		String cookieValue = null;
        if (cookies != null)
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) cookieValue = cookie.getValue();
            }
        return cookieValue;
	}
	
	public static void setAuthentication(String username, String roles, HttpServletRequest request){
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            roles = roles.replace('[', ' ').replace(']', ' ').trim();
            List<String> roleList = Arrays.asList(roles.split(", "));

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
                    null, roleList.stream().map(SimpleGrantedAuthority::new).toList());
            token.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }
}
