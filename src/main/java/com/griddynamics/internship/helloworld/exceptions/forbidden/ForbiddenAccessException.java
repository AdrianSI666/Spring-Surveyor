package com.griddynamics.internship.helloworld.exceptions.forbidden;

/**
 * Class which represents denials of access to user to resources which are not available at this moment in time.
 */
public class ForbiddenAccessException extends RuntimeException {

    /**
     * @param message exception message which tells what exactly happened.
     */
    public ForbiddenAccessException(String message) {
        super(message);
    }

    public ForbiddenAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Method to overwrite stack trace, so when exception occurs, which is delegated to frontend,
     * won't fill server log with stack trace of known exception.
     *
     * @return ignored
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
