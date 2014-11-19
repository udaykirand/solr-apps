package com.search.exceptions;

public class InvalidReutersQueryParameterException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2716306294504138821L;

    public InvalidReutersQueryParameterException() {
	super();
    }

    /**
     * Class Constructor accepting a message to display/log
     */
    public InvalidReutersQueryParameterException(String message) {
	super(message);
    }

    public InvalidReutersQueryParameterException(String message, Throwable cause) {
	super(message, cause);
    }

}
