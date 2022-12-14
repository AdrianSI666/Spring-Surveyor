package com.griddynamics.internship.helloworld.exceptions.taken.passcode;


public class PasscodeTakenException extends RuntimeException {

    public PasscodeTakenException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
