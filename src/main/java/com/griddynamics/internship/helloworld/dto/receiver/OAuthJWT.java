package com.griddynamics.internship.helloworld.dto.receiver;

public record OAuthJWT(
        String clientId,
        String credential,
        String select_by
) {
}
