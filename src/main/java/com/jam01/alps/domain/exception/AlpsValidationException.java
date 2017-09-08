package com.jam01.alps.domain.exception;

/**
 * Created by jam01 on 4/8/17.
 */
public class AlpsValidationException extends RuntimeException {
	public AlpsValidationException(String message) {
		super(message);
	}

	public AlpsValidationException(String message, Exception exception) {
		super(message, exception);
	}
}
