package com.griddynamics.internship.helloworld.dto.receiver;

/**
 * DTO object used for receiving data from the POST and PUT requests. Contains all the
 * fields that can be defined by the user when creating a new user.
 * How user is defined is described in {@link com.griddynamics.internship.helloworld.domain.User}.
 *
 * @param name    - name of the user
 * @param surname - surname of the user
 */
public record UserReceiverDTO(
        String name,
        String surname
) {
}
