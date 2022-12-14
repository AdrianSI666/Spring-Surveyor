package com.griddynamics.internship.helloworld.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Class for single authorization with GoogleIdToken. It's of no use if working with full security service.
 */
@Component
public class GoogleTokenVerifier implements TokenVerifier {
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
            new GsonFactory())
            .setAudience(Collections.singletonList(System.getenv("clientid")))
            .build();

    @Override
    public GoogleIdToken verify(String token) {
        try {
            return verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            throw new ForbiddenAccessException("Given token can't be verified by GoogleIdTokenVerifier.", e);
        }
    }
}
