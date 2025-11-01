package com.opus.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException
{
	public UserAlreadyExistsException(String message)
	{
		super(ErrorConstants.USER_ALREADY_EXISTS, message, HttpStatus.CONFLICT);
	}
}
