package com.griddynamics.internship.helloworld.dto.sender;

/**
 * DTO object used for sending the data to the client using GET requests. Contains all the
 * fields that can be presented to the api user, but not necessarily changed.
 * How participant is defined is described in {@link com.griddynamics.internship.helloworld.domain.Participant}.
 *
 * @param id       - id of the participant
 * @param nick - nickname of the participant
 * @param surveyId - id of the survey attended by the participant
 */
public record ParticipantSenderDTO(
        Long id,
        String nick,
        Long surveyId
) {
}
