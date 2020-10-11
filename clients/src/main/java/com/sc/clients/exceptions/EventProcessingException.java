package com.sc.clients.exceptions;


public class EventProcessingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SdkBaseException with the specified message, and root
     * cause.
     *
     * @param message An error message describing why this exception was thrown.
     * @param t       The underlying cause of this exception.
     */
    public EventProcessingException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Creates a new SdkBaseException with the specified message.
     *
     * @param message An error message describing why this exception was thrown.
     */
    public EventProcessingException(String message) {
        super(message);
    }

    /**
     * Creates a new SdkBaseException with the root cause.
     *
     * @param t The underlying cause of this exception.
     */
    public EventProcessingException(Throwable t) {
        super(t);
    }
}

