package com.griddynamics.internship.helloworld.dto.sender;

/**
 * DTO object used for sending the data to the client using GET requests. Contains all the
 * fields that can be presented to the api user, but not necessarily changed.
 * How user is defined is described in {@link com.griddynamics.internship.helloworld.domain.User}.
 *
 * @param id      - id of the user
 * @param name    - name of the user
 * @param surname - surname of the user
 */
public record UserSenderDTO(
        String id,
        String name,
        String surname
) {
}
