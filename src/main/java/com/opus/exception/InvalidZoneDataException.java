package com.opus.exception;

import org.springframework.http.HttpStatus;

public class InvalidZoneDataException extends BaseException {
	public InvalidZoneDataException(String message) {
		super(ErrorConstants.INVALID_ZONE_DATA, message, HttpStatus.BAD_REQUEST);
	}
}

