package com.opus.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.opus.model.User;
import com.opus.repository.UserRepository;
import com.opus.util.JWTUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{

	JWTUtil jwtUtil;
	UserRepository userRepository;

	@Autowired
	public JwtAuthenticationFilter(JWTUtil jwtUtil, UserRepository userRepository)
	{
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
	{
		String path = request.getServletPath();

		if(path.equals("/api/users/signup") || path.equals("/api/users/login"))
		{
			filterChain.doFilter(request, response);
			return;
		}

		final String header = request.getHeader("Authorization");

		if(header == null || !header.startsWith("Bearer"))
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String token = header.substring(7);

		if(!jwtUtil.validateToken(token))
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Long userId = jwtUtil.extractUserId(token);

		User user = userRepository.findById(userId).orElse(null);
		if(user == null)
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
			user, null, Collections.emptyList()
		);

		SecurityContextHolder.getContext().setAuthentication(authToken);
		filterChain.doFilter(request, response);
	}
}
