package com.opus.exception;

import org.springframework.http.HttpStatus;

public class ZoneNotFoundException extends BaseException {
	public ZoneNotFoundException() {
		super(ErrorConstants.ZONE_NOT_FOUND, "Zone not found", HttpStatus.NOT_FOUND);
	}
}

