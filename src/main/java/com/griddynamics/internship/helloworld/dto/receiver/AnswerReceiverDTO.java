package com.griddynamics.internship.helloworld.dto.receiver;

/**
 * DTO object used for receiving data from the POST and PUT requests. Contains all the
 * fields that can be defined by the user when creating a new answer.
 * How answer is defined is described in {@link com.griddynamics.internship.helloworld.domain.Answer}.
 *
 * @param answer        - answer submitted to the question
 * @param questionId    - id of the question that is being answered
 * @param participantId - id of the survey's participant who submits an answer
 */
public record AnswerReceiverDTO(
        String answer,
        Long questionId,
        Long participantId,
        Long closedAnswerId) {
}
