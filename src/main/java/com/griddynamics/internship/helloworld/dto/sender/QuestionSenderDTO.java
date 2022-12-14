package com.griddynamics.internship.helloworld.dto.sender;

/**
 * DTO object used for sending the data to the client using GET requests. Contains all the
 * fields that can be presented to the api user, but not necessarily changed.
 * How question is defined is described in {@link com.griddynamics.internship.helloworld.domain.Question}.
 *
 * @param id       - id of the question
 * @param name     - name of the question
 * @param content  - body of the question
 * @param hours    - hours part of the time set for each one of the question
 * @param minutes  - minutes part of the time set for each one of the question
 * @param seconds  - seconds part of the time set for each one of the question
 * @param surveyId - id of the survey the question is in
 */
public record QuestionSenderDTO(
        Long id,
        String name,
        String content,
        Integer hours,
        Integer minutes,
        Integer seconds,
        Long surveyId
) {
}
