package com.griddynamics.internship.helloworld.exceptions;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Record for data that will be sent when exception is thrown.
 *
 * @param message       exception message
 * @param httpStatus    status code of exception thrown which determines the type of exception thrown
 * @param zonedDateTime time with timezone showing when this exception occurred
 */
public record ExceptionData(String message,
                            HttpStatus httpStatus,
                            ZonedDateTime zonedDateTime) {
}
