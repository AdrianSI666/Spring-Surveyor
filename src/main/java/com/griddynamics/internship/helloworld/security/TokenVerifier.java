package com.griddynamics.internship.helloworld.security;

import com.google.api.client.auth.openidconnect.IdToken;

public interface TokenVerifier {
    IdToken verify(String token);
}
