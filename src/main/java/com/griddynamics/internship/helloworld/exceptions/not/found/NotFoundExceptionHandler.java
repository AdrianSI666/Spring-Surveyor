package com.griddynamics.internship.helloworld.exceptions.not.found;

import com.griddynamics.internship.helloworld.exceptions.ExceptionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

/**
 * Exception handler that handle NotFoundException exceptions that occurs when
 * user wants to get data that doesn't exist.
 */
@ControllerAdvice
@Slf4j
public class NotFoundExceptionHandler {
    private static final String MESSAGE = "The requested data could not be found";

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        log.error("Object not found. Exception occurred: %s".formatted(e.getMessage()), e);
        ExceptionData exceptionData = new ExceptionData(MESSAGE, httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
