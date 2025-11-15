package com.opus.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException
{
	public InvalidCredentialsException(String message)
	{
		super(ErrorConstants.INVALID_CREDENTIALS, message, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
	}
}
