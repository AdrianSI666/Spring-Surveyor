package com.griddynamics.internship.helloworld.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

/**
 * Class for Oauth2ResourceServer. It's needed if we want to use oauth2ResourceServer, since it's using JWT decoder with every
 * protected endpoint. There are some automatic decoders, but I didn't find any that worked with this kind of JWT from Google.
 * <p>
 * Also Google didn't send claims in the token response, so I needed to add them manually, since it won't run without claims.
 */
@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> userData;
        Map<String, Object> headers;
        try {
            userData = mapper.readValue(payload, Map.class);
            headers = mapper.readValue(header, Map.class);
        } catch (JsonProcessingException e) {
            throw new InvalidBearerTokenException("Failed to read headers or user data from given token:" + token);
        }
        return new Jwt(token, Instant.ofEpochSecond((Integer) userData.get("iat")),
                Instant.ofEpochSecond((Integer) userData.get("exp")),
                headers,
                Map.of("email", true, "profile", true, "openid", true));
    }
}
