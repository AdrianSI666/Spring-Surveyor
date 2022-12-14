package com.griddynamics.internship.helloworld.exceptions.taken.passcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

/**
 * Exception handler created for {@link PasscodeTakenException}.
 * When exception occurs it returns an HTTP conflict status as ResponseEntity
 */
@ControllerAdvice
@Slf4j
public class PasscodeTakenHandler {
    @ExceptionHandler(value = {PasscodeTakenException.class})
    public ResponseEntity<Object> passcodeTakenHandle(PasscodeTakenException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        PasscodeTakenData pteData = new PasscodeTakenData(e.getMessage(), httpStatus, ZonedDateTime.now());
        log.error("Passcode already taken", e);
        return new ResponseEntity<>(pteData, httpStatus);
    }
}
