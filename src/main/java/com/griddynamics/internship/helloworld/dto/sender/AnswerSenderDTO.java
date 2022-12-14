package com.griddynamics.internship.helloworld.dto.sender;

/**
 * DTO object used for sending the data to the client using GET requests. Contains all the
 * fields that can be presented to the api user, but not necessarily changed.
 * How answer is defined is described in {@link com.griddynamics.internship.helloworld.domain.Answer}.
 *
 * @param content              - answer submitted to the question
 * @param question_id          - id of the question that is being answered
 * @param question_name        - name of the question that is being answered
 * @param question_context     - body of the question
 * @param participant_id       - id of the participant that wrote the answer
 * @param participant_nickname - nickname of the participant that wrote the answer
 */
public record AnswerSenderDTO(
        String content,
        Long question_id,
        String question_name,
        String question_context,
        Long participant_id,
        String participant_nickname,
        Long closedAnswer_id,
        String closedAnswer_content
) {
}
