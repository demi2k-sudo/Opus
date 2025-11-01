package com.opus.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
	private final int code;
	private final HttpStatus status;

	protected BaseException(int code, String message, HttpStatus status) {
		super(message);
		this.code = code;
		this.status = status;
	}

	public int getCode()
	{
		return code;
	}
	public HttpStatus getStatus()
	{
		return status;
	}
}
