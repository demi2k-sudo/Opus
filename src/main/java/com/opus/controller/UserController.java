package com.opus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opus.dto.SignUpRequest;
import com.opus.service.UserService;

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
}
