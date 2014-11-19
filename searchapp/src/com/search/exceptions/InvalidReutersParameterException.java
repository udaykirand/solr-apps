package com.search.exceptions;

public class InvalidReutersParameterException extends Exception {

    private static final long serialVersionUID = -330242331074586777L;

    public InvalidReutersParameterException() {
	super();
    }

    /**
     * Class Constructor accepting a message to display/log
     */
    public InvalidReutersParameterException(String message) {
	super(message);
    }

    public InvalidReutersParameterException(String message, Throwable cause) {
	super(message, cause);
    }

}
