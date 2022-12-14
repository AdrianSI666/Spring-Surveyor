package com.griddynamics.internship.helloworld.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.GCodeToken;
import com.griddynamics.internship.helloworld.dto.receiver.UserReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserOAuthSenderDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.conflict.DataConflictException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.UserMapperImpl;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.security.GoogleTokenVerifier;
import com.griddynamics.internship.helloworld.specification.SearchCriteria;
import com.griddynamics.internship.helloworld.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.griddynamics.internship.helloworld.service.SurveyService.SURVEY_NOT_FOUND_MSG;

/**
 * Service layer for all business actions regarding user entity.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    /**
     * Mapper for mapping user entity to it's dto representation.
     */
    private final UserMapperImpl mapper;

    protected static final String USER_NOT_FOUND_MSG = "Couldn't find user with %s id";

    private final GoogleTokenVerifier tokenVerifier;

    /**
     * Finds all the users that exist in the database.
     *
     * @return list with the user dto representations.
     */
    public Page<UserSenderDTO> getUsers(Integer page) {
        log.info("Found all users");

        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<User> userPage = userRepository.findAll(paging);
        return userPage.map(mapper::map);
    }

    /**
     * Login as account from Google or if user never logged before add it to a database and login.
     *
     * @param code - token acquired from Google while logging from Google SSO.
     * @return credentials that are used as JWT access token.
     */
    public UserOAuthSenderDTO addUser(GCodeToken code) throws IOException {
        final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow =
                new GoogleAuthorizationCodeFlow(
                        new NetHttpTransport(),
                        new GsonFactory(),
                        System.getenv("clientid"),
                        System.getenv("clientsecret"),
                        List.of("openid", "profile", "email"));
        GoogleAuthorizationCodeTokenRequest tokenRequest = googleAuthorizationCodeFlow.newTokenRequest(code.code());
        tokenRequest.setRedirectUri("postmessage");
        GoogleTokenResponse tokenResponse = tokenRequest.execute();
        String token = tokenResponse.getIdToken();
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userData = objectMapper.readValue(payload, Map.class);
        Map<String, Object> headerData = objectMapper.readValue(header, Map.class);
        userData.forEach((key, value) -> log.info("USER key = " + key + " value = " + value));
        headerData.forEach((key, value) -> log.info("HEADER key = " + key + " value = " + value));
        if (!userRepository.existsById((String) userData.get("sub"))) {
            log.info("Adding user: {}", userData);
            userRepository.save(User.builder()
                    .id((String) userData.get("sub"))
                    //We shouldn't use sub as primary id,
                    // but as secondary field and still have serial id in database,
                    // but as we don't have time I will just skip it and use it
                    // also it's good to add another field to check from the user logged in
                    .name((String) userData.get("given_name"))
                    .surname((String) userData.get("family_name"))
                    .createdSurveys(new ArrayList<>()).build());
        }
        log.info("User with id {} logged in", userData.get("sub"));
        return new UserOAuthSenderDTO((String) userData.get("sub"), token);
        //Code for handling authorization to single access by Google, doesn't work with Spring Security
        /*GoogleIdToken idToken = tokenVerifier.verify(code.credential());
        if (idToken == null) {
            throw new ForbiddenAccessException("Somebody tried to log in as user not by google SSO");
        }
        Payload payload = idToken.getPayload();
        String userId = payload.getSubject();
        if (!userRepository.existsById(userId)) {
            User user = User.builder()
                    .id(userId)
                    .name((String) payload.get("name"))
                    .surname((String) payload.get("family_name"))
                    .build();
            userRepository.save(user);
        }
        log.info(userId);
        return idToken;*/

    }

    /**
     * Finds a user that is the author of the survey.
     *
     * @param surveyId - id of the survey.
     * @return dto representation of the user.
     */
    public UserSenderDTO getUserBySurveyId(Long surveyId) {
        log.info("Finding user by survey id: {}", surveyId);
        UserSpecification specById = new UserSpecification(new SearchCriteria("createdSurveys_id", ":", surveyId));
        User user = userRepository.findOne(specById).orElseThrow(
                () -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyId)));
        return mapper.map(user);
    }

    /**
     * Finds the user in the database.
     *
     * @param userID - id of the user
     * @return dto representation of the user.
     */
    public UserSenderDTO getUser(String userID) {
        log.info("Finding user with id: {}", userID);

        return mapper.map(userRepository.findById(userID)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userID))));
    }

    /**
     * Updates existing user in the database.
     *
     * @param userID          - id of the user
     * @param userReceiverDTO - dto with all the fields necessary to update user in the database.
     * @return dto representation of the updated user.
     */
    public UserSenderDTO updateUser(String userID, UserReceiverDTO userReceiverDTO) {
        log.info("Updating user with id: {}", userID);

        userRepository.findByNameAndSurname(userReceiverDTO.name(), userReceiverDTO.surname())
                .ifPresent(user -> {
                    throw new DataConflictException("Username with name: %s and surname: %s is already present"
                            .formatted(userReceiverDTO.name(), userReceiverDTO.surname()));
                });

        User userToUpdate = userRepository.findById(userID)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userID)));

        log.debug("Old user: {}. New user: {}", userToUpdate, userReceiverDTO);

        userToUpdate.setName(userReceiverDTO.name());
        userToUpdate.setSurname(userReceiverDTO.surname());
        return mapper.map(userRepository.save(userToUpdate));
    }

    /**
     * Deletes user from the database
     *
     * @param userID - id of the user
     */
    public void deleteUser(String userID) {
        log.info("Deleting user with id: {}", userID);
        userRepository.deleteById(userID);
    }

    /**
     * Logs the user in to the system.
     *
     * @param name    - name of the user.
     * @param surname - surname of the user.
     * @return dto representation of the user.
     */
    public UserSenderDTO login(String name, String surname) {
        log.info("Login user with name: %s and surname: %s".formatted(name, surname));
        UserSpecification specByName = new UserSpecification(new SearchCriteria("name", ":", name));
        UserSpecification specBySurname = new UserSpecification(new SearchCriteria("surname", ":", surname));
        return mapper.map(userRepository.findOne(Specification.where(specByName).and(specBySurname))
                .orElseThrow(
                        () -> {
                            log.error("Couldn't find user with name %s".formatted(name));
                            return new NotFoundException("Couldn't find user with name %s".formatted(name));
                        }));
    }
}
