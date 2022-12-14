package com.griddynamics.internship.helloworld.controller;

import com.griddynamics.internship.helloworld.dto.receiver.GCodeToken;
import com.griddynamics.internship.helloworld.dto.receiver.UserReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserOAuthSenderDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class is a REST controller for user resource. It does not return or receive entity objects. Instead, it
 * uses appropriate receiver and sender dto objects which present only those fields which are converted to JSON.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(Path.USER_VALUE + "s")
public class UserController {

    private final UserService userService;

    /**
     * Makes a call to the service layer to create a user entity in the database.
     *
     * @param code - dto with all the customizable fields of the user entity class.
     * @return http Response with the dto representation of user with the 201 CREATED status code
     * if there aren't any exceptions.
     */
    @PostMapping()
    @CrossOrigin(origins = {"*"}, maxAge = 4800, allowCredentials = "false")
    public ResponseEntity<UserOAuthSenderDTO> addUser(@RequestBody GCodeToken code) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(code));
        } catch (IOException e) {
            throw new ForbiddenAccessException("Login with: %s failed".formatted(code.code()), e);
        }
    }

    /**
     * Makes a call to the service layer to find all the users in the database.
     *
     * @return http Response with the dto representations of the users with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(defaultValue = "1") Integer page) {
        Page<UserSenderDTO> userSenderDTOPage = userService.getUsers(page);
        List<UserSenderDTO> users = userSenderDTOPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("currentPage", userSenderDTOPage.getNumber() + 1);
        response.put("totalPages", userSenderDTOPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    /**
     * Makes a call to receive specific user by id.
     *
     * @param id - id of the user.
     * @return http Response with the dto representation of the user with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserSenderDTO> getUser(@PathVariable String id) throws NotFoundException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    /**
     * Makes a call to the service layer to find the author of the survey.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representations of the user with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.SURVEY_VALUE + "/{id}")
    public ResponseEntity<UserSenderDTO> getUserBySurveyId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserBySurveyId(id));
    }

    /**
     * Makes a call to the service layer to update an existing user in the database.
     *
     * @param user - dto with all the customizable fields of the user entity class
     * @return http Response with the dto representation of user with the 200 OK status code
     * if there aren't any exceptions.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserSenderDTO> updateUser(@PathVariable String id, @RequestBody UserReceiverDTO user) {
        return ResponseEntity.ok().body(userService.updateUser(id, user));
    }

    /**
     * Makes a call to the service layer to delete a user based on his id.
     *
     * @param id - id of the user.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to create a user in the database.
     *
     * @param name    - name of the user.
     * @param surname - surname of the user.
     * @return dto representation of the created user with the 200 OK status code if there aren't any exceptions.
     */
    @GetMapping("/{name}/{surname}")
    public ResponseEntity<UserSenderDTO> login(@PathVariable("name") String name,
                                               @PathVariable("surname") String surname) {
        return ResponseEntity.ok().body(userService.login(name, surname));
    }
}
