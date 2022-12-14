package com.griddynamics.internship.helloworld.exceptions.forbidden;

import com.griddynamics.internship.helloworld.exceptions.ExceptionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

/**
 * Exception handler that handle ForbiddenAccessException exceptions that occurs when
 * user wants to get to resources which are not available for him at this moment.
 */
@ControllerAdvice
@Slf4j
public class ForbiddenAccessExceptionHandler {
    private static final String MESSAGE = "The requested resources are not available";

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {ForbiddenAccessException.class})
    public ResponseEntity<Object> handleForbiddenAccess(ForbiddenAccessException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        log.error("Can't proceed with given request, reason: " + e.getMessage(), e);
        ExceptionData exceptionData = new ExceptionData(MESSAGE, httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
