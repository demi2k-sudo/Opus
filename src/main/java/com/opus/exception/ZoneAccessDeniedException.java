package com.opus.exception;

import org.springframework.http.HttpStatus;

public class ZoneAccessDeniedException extends BaseException {
	public ZoneAccessDeniedException() {
		super(ErrorConstants.ZONE_ACCESS_DENIED, "You do not have access to this zone", HttpStatus.FORBIDDEN);
	}
}

