package com.griddynamics.internship.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.dto.receiver.UserReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.exceptions.ExceptionData;
import com.griddynamics.internship.helloworld.exceptions.conflict.DataConflictException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
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

    private final List<UserSenderDTO> userSenderDTOList = List.of(userSenderDTO, userSenderDTOSecond);

    private final String USER_MAIN_PATH = Path.USER_VALUE + "s";

    @Test
    void getUser_by_id_test() throws Exception {
//        given(userService.getUser(userSenderDTO.id())).willReturn(userSenderDTO);

        mockMvc.perform(get(USER_MAIN_PATH + "/{id}", userSenderDTO.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userSenderDTO.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userSenderDTO.name())))
                .andExpect(jsonPath("$.surname", is(userSenderDTO.surname())));
    }

    @Test
    void addUser_test() throws Exception {
//        given(userService.addUser(userReceiverDTO)).willReturn(userSenderDTO);

        String userAsString = new ObjectMapper().writeValueAsString(userReceiverDTO);

        mockMvc.perform(post(USER_MAIN_PATH + "")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userAsString))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userSenderDTO.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userSenderDTO.name())))
                .andExpect(jsonPath("$.surname", is(userSenderDTO.surname())));

    }

    @Test
    void deleteUser_test() throws Exception {
        mockMvc.perform(delete((USER_MAIN_PATH + "/{id}"), userSenderDTO.id()))
                .andExpect(status().isOk());
    }

    @Test
    void getUsers_test() throws Exception {
        given(userService.getUsers(1))
                .willReturn(new PageImpl<>(userSenderDTOList));

        mockMvc.perform(get(USER_MAIN_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPage", is(1), Integer.class))
                .andExpect(jsonPath("$.totalPages", is(1), Integer.class))
                .andExpect(jsonPath("$.users.[0].id", is(userSenderDTO.id()), Long.class))
                .andExpect(jsonPath("$.users.[0].name", is(userSenderDTO.name())))
                .andExpect(jsonPath("$.users.[0].surname", is(userSenderDTO.surname())));
    }

    @Test
    void getUserBySurveyId_test() throws Exception {
        given(userService.getUserBySurveyId(survey.getId())).willReturn(userSenderDTOSecond);

        mockMvc.perform(get(USER_MAIN_PATH + Path.SURVEY_VALUE + "/{id}", survey.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userSenderDTOSecond.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userSenderDTOSecond.name())))
                .andExpect(jsonPath("$.surname", is(userSenderDTOSecond.surname())));
    }

    @Test
    void updateUser_test() throws Exception {
        UserReceiverDTO newUser = new UserReceiverDTO("newName", "newSurname");
        UserSenderDTO updatedUser = new UserSenderDTO(userSenderDTO.id(), newUser.name(), newUser.surname());
        given(userService.updateUser(userSenderDTO.id(), newUser)).willReturn(updatedUser);

        String content = new ObjectMapper().writeValueAsString(newUser);

        mockMvc.perform(post(USER_MAIN_PATH + "/{id}", userSenderDTO.id())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.id()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.name())))
                .andExpect(jsonPath("$.surname", is(updatedUser.surname())));
    }

    @Test
    void getUser_status_not_found() throws Exception {
        NotFoundException notFoundException = new NotFoundException("Can't find data which was request");
//        given(userService.getUser(userSenderDTO.id())).willThrow(notFoundException);

        mockMvc.perform(get(USER_MAIN_PATH + "/{id}", userSenderDTO.id()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("The requested data could not be found")));
    }

    @Test
    void login() throws Exception {
        given(userService.login(userSenderDTO.name(), userSenderDTO.surname())).willReturn(userSenderDTO);

        mockMvc.perform(get(USER_MAIN_PATH + "/{name}/{surname}", userSenderDTO.name(), userSenderDTO.surname()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userSenderDTO.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userSenderDTO.name())))
                .andExpect(jsonPath("$.surname", is(userSenderDTO.surname())));
    }

    @Test
    void addUserHandleDataConflictException() throws Exception {
        DataConflictException exception = new DataConflictException(
                "Username with name: %s and surname: %s is already present"
                        .formatted(userReceiverDTO.name(), userReceiverDTO.surname()));
//        given(userService.addUser(userReceiverDTO)).willThrow(exception);

        ExceptionData exceptionData = new ExceptionData(exception.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now());
        String userAsString = new ObjectMapper().writeValueAsString(userReceiverDTO);

        mockMvc.perform(post(USER_MAIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userAsString))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataConflictException))
                .andExpect(result ->
                        Assertions.assertEquals(exception.getMessage(),
                                result.getResolvedException().getMessage()))
                .andExpect(result -> result.getResponse()
                        .equals(new ResponseEntity<>(exceptionData, HttpStatus.CONFLICT)));
    }
}
