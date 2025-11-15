package com.opus.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException
{
	public UserNotFoundException(String message)
	{
		super(ErrorConstants.USER_NOT_FOUND, message, HttpStatus.NOT_FOUND);
	}
}
