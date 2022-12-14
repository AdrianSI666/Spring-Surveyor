package com.griddynamics.internship.helloworld.exceptions.conflict;

import com.griddynamics.internship.helloworld.exceptions.ExceptionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

/**
 * Exception handler that handle DataConflictException exceptions that occurs when
 * receiving data creates conflicts with existing data from the database.
 */
@ControllerAdvice
@Slf4j
public class DataConflictExceptionHandler {
    private static final String MESSAGE = "Given data cannot be processed due to conflict with existing data";

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {DataConflictException.class})
    public ResponseEntity<Object> handleDataConflict(DataConflictException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        log.error("Conflict with already existing data occurred: " + e.getMessage(), e);
        ExceptionData exceptionData = new ExceptionData(MESSAGE, httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
