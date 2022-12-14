package com.griddynamics.internship.helloworld.dto.sender;

/**
 * DTO object used for sending the data to the client using GET requests. Contains all the
 * fields that can be presented to the api user, but not necessarily changed.
 * How survey is defined is described in {@link com.griddynamics.internship.helloworld.domain.Survey}.
 *
 * @param surveyId      - id of the survey
 * @param name          - name of the survey
 * @param description   - description of the survey
 * @param passcode      - passcode used to join the survey
 * @param hours         - hours part of the time set for each one of the survey's questions
 * @param minutes       - minutes part of the time set for each one of the survey's questions
 * @param seconds       - seconds part of the time set for each one of the survey's questions
 * @param authorId      - id of the user that created the survey
 * @param authorName    - name of the user that created the survey
 * @param authorSurname - surname of the user that created the survey
 * @param surveyStarted - flag used to determine if the survey has started
 */
public record SurveySenderDTO(
        Long surveyId,
        String name,
        String description,
        String passcode,
        Integer hours,
        Integer minutes,
        Integer seconds,
        String authorId,
        String authorName,
        String authorSurname,
        Boolean surveyStarted
) {
}
