package com.opus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opus.dto.LoginRequest;
import com.opus.dto.LoginResponse;
import com.opus.dto.SignUpRequest;
import com.opus.dto.UserDetailsResponse;
import com.opus.model.User;
import com.opus.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController
{
	private UserService userService;

	@Autowired
	public UserController(UserService userService)
	{
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ResponseEntity<String> signupUser(@RequestBody SignUpRequest signUpRequest) throws Exception
	{
		userService.signupUser(signUpRequest);
		return ResponseEntity.ok("Signup Successful!");
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest)
	{
		LoginResponse loginResponse = userService.loginUser(loginRequest);
		return ResponseEntity.ok(loginResponse);
	}

	@GetMapping("/me")
	public ResponseEntity<UserDetailsResponse> getLoggedInUser(Authentication authentication)
	{
		User user = (User) authentication.getPrincipal();

		return ResponseEntity.ok(UserDetailsResponse.builder()
			.username(user.getUsername())
			.email(user.getEmail())
			.name(user.getName()).build());
	}
}
