package com.griddynamics.internship.helloworld.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.HelloWorldApplication;
import com.griddynamics.internship.helloworld.controller.UserController;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.dto.receiver.GCodeToken;
import com.griddynamics.internship.helloworld.dto.receiver.UserReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.conflict.DataConflictException;
import com.griddynamics.internship.helloworld.exceptions.conflict.DataConflictExceptionHandler;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessExceptionHandler;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundExceptionHandler;
import com.griddynamics.internship.helloworld.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {HelloWorldApplication.class,
        DataConflictExceptionHandler.class,
        ForbiddenAccessExceptionHandler.class,
        NotFoundExceptionHandler.class})
@ActiveProfiles("test")
@WebAppConfiguration
class ExceptionHandlerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    private final UserReceiverDTO userReceiverDTO = new UserReceiverDTO("test", "testSurname");
    private final UserSenderDTO userSenderDTO = new UserSenderDTO("1L",
            userReceiverDTO.name(),
            userReceiverDTO.surname());
    private final Survey survey = Survey.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .passcode(null)
            .duration(LocalTime.of(0, 5, 0))
            .questions(null)
            .author(null)
            .participants(null)
            .started(false)
            .build();
    private final UserSenderDTO userSenderDTOSecond = new UserSenderDTO("2L",
            userReceiverDTO.name(),
            userReceiverDTO.surname());

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void userControllerHandleDataConflictException() throws Exception {
        given(userService.addUser(any(GCodeToken.class))).willThrow(
                new DataConflictException("Username with name: %s and surname: %s is already present"
                        .formatted(userSenderDTO.name(),userSenderDTO.surname())));
        DataConflictException exception = new DataConflictException(
                "Username with name: %s and surname: %s is already present"
                        .formatted(userReceiverDTO.name(), userReceiverDTO.surname()));
//        given(userService.addUser(userReceiverDTO)).willThrow(exception);

        ExceptionData exceptionData = new ExceptionData(exception.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now());
        String userAsString = new ObjectMapper().writeValueAsString(userReceiverDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userAsString))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataConflictException))
                .andExpect(result ->
                        Assertions.assertEquals(exception.getMessage(),
                                Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(result -> result.getResponse()
                        .equals(new ResponseEntity<>(exceptionData, HttpStatus.CONFLICT)));
    }

    @Test
    void userControllerHandleForbiddenAccessException() throws Exception {
        given(userService.addUser(any(GCodeToken.class))).willThrow(
                new ForbiddenAccessException("Can't create user, server is down."));
        ForbiddenAccessException exception = new ForbiddenAccessException("Can't create user, server is down.");
//        given(userService.addUser(userReceiverDTO)).willThrow(exception);

        ExceptionData exceptionData = new ExceptionData(exception.getMessage(),
                HttpStatus.FORBIDDEN,
                ZonedDateTime.now());
        String userAsString = new ObjectMapper().writeValueAsString(userReceiverDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userAsString))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenAccessException))
                .andExpect(result ->
                        Assertions.assertEquals(exception.getMessage(),
                                Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(result -> result.getResponse()
                        .equals(new ResponseEntity<>(exceptionData, HttpStatus.FORBIDDEN)));
    }

    @Test
    void userControllerHandleNotFoundException() throws Exception {
        given(userService.getUser(anyString())).willThrow(
                new NotFoundException("Couldn't find user with %b id".formatted(userSenderDTO.id())));
        NotFoundException exception = new NotFoundException("Couldn't find user with %b id".formatted(userSenderDTO.id()));
//        given(userService.getUser(userSenderDTO.id())).willThrow(exception);

        ExceptionData exceptionData = new ExceptionData(exception.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now());
        String userAsString = new ObjectMapper().writeValueAsString(userReceiverDTO);

        mockMvc.perform(get("/users/%b".formatted(userSenderDTO.id())))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result ->
                        Assertions.assertEquals(exception.getMessage(),
                                Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(result -> result.getResponse()
                        .equals(new ResponseEntity<>(exceptionData, HttpStatus.NOT_FOUND)));
    }
}
