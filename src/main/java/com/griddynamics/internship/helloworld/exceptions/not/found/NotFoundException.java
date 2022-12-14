package com.griddynamics.internship.helloworld.exceptions.not.found;

/**
 * Class which represents that user wants to get data that doesn't exist.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
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
