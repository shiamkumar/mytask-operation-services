package com.ghx.api.operations.exception;

/**
 * SystemException Class for Internal Server Errors
 * @author Ananth Kandasamy
 *
 */
public class SystemException extends RuntimeException {

    /**
     * Default Serial Version UID
     */
    private static final long serialVersionUID = 2081116140198832277L;

    /**
     * Default Constructor
     */
    public SystemException() {
        super();
    }

    /**
     * Constructor with Error Message and Throwable cause
     * @param message
     * @param cause
     */
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with only Error Message
     * @param message
     */
    public SystemException(String message) {
        super(message);
    }

    /**
     * Constructor with only Throwable cause
     * @param cause
     */
    public SystemException(Throwable cause) {
        super(cause);
    }

}
