package com.opus.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil
{
	private final Key key;
	private final Long expirationMs;

	public JWTUtil(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.expiration-ms}") long expirationMs
	) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.expirationMs = expirationMs;
	}

	public String generateToken(Long userId)
	{
		return Jwts.builder().setSubject(String.valueOf(userId))
		.setIssuedAt(new Date())
		.setExpiration(new Date(System.currentTimeMillis() + expirationMs))
		.signWith(key)
		.compact();
	}

	public Long extractUserId(String token)
	{
		String subject = Jwts.parserBuilder().setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
		return Long.parseLong(subject);
	}

	public boolean validateToken(String token)
	{
		try
		{
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build().parseClaimsJws(token);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
