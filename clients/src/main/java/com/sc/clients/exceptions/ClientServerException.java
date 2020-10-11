package com.sc.clients.exceptions;


public class ClientServerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SdkBaseException with the specified message, and root
     * cause.
     *
     * @param message An error message describing why this exception was thrown.
     * @param t       The underlying cause of this exception.
     */
    public ClientServerException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Creates a new SdkBaseException with the specified message.
     *
     * @param message An error message describing why this exception was thrown.
     */
    public ClientServerException(String message) {
        super(message);
    }

    /**
     * Creates a new SdkBaseException with the root cause.
     *
     * @param t The underlying cause of this exception.
     */
    public ClientServerException(Throwable t) {
        super(t);
    }
}

