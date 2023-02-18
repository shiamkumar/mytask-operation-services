package com.ghx.api.operations.exception;


/**
 * 
 * @author Rajasekar Jayakumar
 *
 */

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1018490689123250430L;
	
	public BusinessException() {
        super();
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    public BusinessException(String message) {
        super(message);
    }
    public BusinessException(Throwable cause) {
        super(cause);
    }
}
