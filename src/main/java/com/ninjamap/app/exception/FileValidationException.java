package com.ninjamap.app.exception;

public class FileValidationException extends RuntimeException 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileValidationException() {
		super();
	}

	public FileValidationException(String message) {
		super(message);
	}

}
