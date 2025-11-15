package com.opus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opus.dto.LoginRequest;
import com.opus.dto.LoginResponse;
import com.opus.dto.SignUpRequest;
import com.opus.exception.InvalidCredentialsException;
import com.opus.exception.UserAlreadyExistsException;
import com.opus.exception.UserNotFoundException;
import com.opus.model.User;
import com.opus.repository.UserRepository;
import com.opus.util.JWTUtil;
import com.opus.util.PasswordUtil;

@Service
public class UserService
{
	private UserRepository userRepository;
	private JWTUtil jwtUtil;
	@Autowired
	public UserService(UserRepository userRepository, JWTUtil jwtUtil)
	{
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
	}


	public void signupUser(SignUpRequest signUpRequest)
	{
		//check if username already exists
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new UserAlreadyExistsException("Username already exists");
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new UserAlreadyExistsException("Email already registered");
		}

		//handle password
		String salt = PasswordUtil.generateSalt();
		String hashedPassword = PasswordUtil.hashPassword(signUpRequest.getPassword(), salt);

		//persist User
		User user = User.builder()
			.name(signUpRequest.getName())
			.username(signUpRequest.getUsername())
			.email(signUpRequest.getEmail())
			.passwordHash(hashedPassword)
			.passwordSalt(salt)
			.build();

		userRepository.save(user);
	}

	public LoginResponse loginUser(LoginRequest loginRequest)
	{
		//find the user
		User user = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
			.or(() -> userRepository.findByEmail(loginRequest.getUsernameOrEmail()))
			.orElseThrow(() -> new UserNotFoundException("User Not Found !"));

		//verify password
		String hashedPassword = PasswordUtil.hashPassword(loginRequest.getPassword(), user.getPasswordSalt());
		if (!hashedPassword.equals(user.getPasswordHash()))
		{
			throw new InvalidCredentialsException("Invalid credentials");
		}

		//generate token
		String token = jwtUtil.generateToken(user.getUserId());

		return LoginResponse.builder().username(user.getUsername()).token(token).build();
	}
}
