package com.ghx.api.operations.exception;
/**
 * class ResourceNotFoundException this is custom exception class for 404 not found hanlde
 * @author ananth.k
 *
 */
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * Default Serial Version UID
	 */
	private static final long serialVersionUID = 4832045769082904496L;

	/**
	 * Default Constructor
	 */
	public ResourceNotFoundException() {
		super();
	}

	/**
	 * Constructor with Error Message and Throwable cause
	 * 
	 * @param message
	 * @param cause
	 */
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with only Error Message
	 * 
	 * @param message
	 */
	public ResourceNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructor with only Throwable cause
	 * 
	 * @param cause
	 */
	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

}
