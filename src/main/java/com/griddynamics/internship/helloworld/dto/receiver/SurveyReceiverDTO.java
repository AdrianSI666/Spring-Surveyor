package com.griddynamics.internship.helloworld.dto.receiver;

/**
 * DTO object used for receiving data from the POST and PUT requests. Contains all the
 * fields that can be defined by the user when creating a new survey.
 * How survey is defined is described in {@link com.griddynamics.internship.helloworld.domain.Survey}.
 *
 * @param name        - name of the survey
 * @param description - survey's description
 * @param hours       - hours part of the time set for each one of the survey's questions
 * @param minutes     - minutes part of the time set for each one of the survey's questions
 * @param seconds     - seconds part of the time set for each one of the survey's questions
 * @param passcode    - passcode used to join the survey
 * @param authorId    - survey's author id
 */
public record SurveyReceiverDTO(
        String name,
        String description,
        Integer hours,
        Integer minutes,
        Integer seconds,
        String passcode,
        String authorId
) {
}
