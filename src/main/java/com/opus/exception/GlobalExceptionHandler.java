package com.opus.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<Object> handleBaseException(BaseException ex) {
		return ResponseEntity.status(ex.getStatus())
			.body(new ErrorResponse(ex.getCode(), ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
        ex.printStackTrace();
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse(ErrorConstants.INTERNAL_SERVER_ERROR, "Something went wrong"));
	}

	@Getter
	@AllArgsConstructor
	public static class ErrorResponse {
		private int code;
		private String message;
	}
}
