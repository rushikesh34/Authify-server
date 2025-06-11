package com.example.Authify.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



@Component
public class JwtUtil {
	
	
	@Value("${jwt.secret.key}")
	private String SECREATE_KEY;
	
	public String generatetoken(UserDetails userdetail) {
		Map<String, Object>claims=new HashMap<>();
		return createtoken(claims,userdetail.getUsername());
	}

	private String createtoken(Map<String, Object> claims, String email) {
		return Jwts.builder()
		.setClaims(claims)
		.setSubject(email)
		.setIssuedAt(new Date(System.currentTimeMillis()))
		.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *10 ))
		.signWith(SignatureAlgorithm.HS256,SECREATE_KEY)
		.compact();
		
	}
	
	private Claims extractAllClaims(String token) {
		
		return Jwts.parser()
				.setSigningKey(SECREATE_KEY)
				.parseClaimsJws(token)
				.getBody();
	}
	
	
	public <T> T extractClaim(String token,Function<Claims,T> claimresolver) {
		
		final Claims claim =extractAllClaims(token);
		return claimresolver.apply(claim);
		
	}
	
	public String extractEmaik(String token) {
		return extractClaim(token, Claims::getSubject);
		
	}
	
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	private boolean istokenExpire(String token) {
		return extractExpiration(token).before(new Date());
		
	}
	
	public boolean validatetoken(String token,UserDetails userdetail) {
		final String email=extractEmaik(token);
		return (email.equals(userdetail.getUsername()) && !istokenExpire(token));
		
	}
	
	

}
