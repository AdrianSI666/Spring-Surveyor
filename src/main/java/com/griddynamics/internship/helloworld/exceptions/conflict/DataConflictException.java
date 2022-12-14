package com.griddynamics.internship.helloworld.exceptions.conflict;

/**
 * Class which represents conflicts in data, like constraints violations.
 */
public class DataConflictException extends RuntimeException {

    /**
     * @param message exception message which tells what exactly happened.
     */
    public DataConflictException(String message) {
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
