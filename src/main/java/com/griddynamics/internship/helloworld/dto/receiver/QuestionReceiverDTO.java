package com.griddynamics.internship.helloworld.dto.receiver;

/**
 * DTO object used for receiving data from the POST and PUT requests. Contains all the
 * fields that can be defined by the user when creating a new question.
 * How question is defined is described in {@link com.griddynamics.internship.helloworld.domain.Question}.
 *
 * @param name     - name of the question
 * @param content  - the body of the question
 * @param surveyId - id of the survey that the question belongs to
 */
public record QuestionReceiverDTO(
        String name,
        String content,
        Long surveyId
) {
}
