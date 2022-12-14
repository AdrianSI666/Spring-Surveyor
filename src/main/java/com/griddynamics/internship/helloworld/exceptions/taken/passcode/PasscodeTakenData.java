package com.griddynamics.internship.helloworld.exceptions.taken.passcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Exception data payload for {@link PasscodeTakenException}
 */
@AllArgsConstructor
@Getter
public class PasscodeTakenData {
    final String message;
    final HttpStatus httpStatus;
    final ZonedDateTime zonedDateTime;
}
