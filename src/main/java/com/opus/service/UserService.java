package com.opus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opus.dto.SignUpRequest;
import com.opus.exception.UserAlreadyExistsException;
import com.opus.model.User;
import com.opus.repository.UserRepository;
import com.opus.util.PasswordUtil;

@Service
public class UserService
{
	@Autowired
	private UserRepository userRepository;

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


}
