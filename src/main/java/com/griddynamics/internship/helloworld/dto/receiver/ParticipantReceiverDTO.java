package com.griddynamics.internship.helloworld.dto.receiver;

/**
 * DTO object used for receiving data from the POST and PUT requests. Contains all the
 * fields that can be defined by the user when creating a new participant.
 * How participant is defined is described in {@link com.griddynamics.internship.helloworld.domain.Participant}.
 *
 * @param nick     - nickname of the participant
 * @param passcode - passcode of the survey that participants wants to join
 */
public record ParticipantReceiverDTO(
        String nick,
        String passcode
) {
}
