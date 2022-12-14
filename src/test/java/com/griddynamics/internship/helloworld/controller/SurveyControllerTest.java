package com.griddynamics.internship.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.SurveyReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveySenderDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveyStatusDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.service.SurveyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = SurveyController.class)
@ActiveProfiles("test")
class SurveyControllerTest {

    @MockBean
    private SurveyService surveyService;

    @Autowired
    private MockMvc mockMvc;
    private final User user = User.builder()
            .id("1L")
            .name("test")
            .surname("testsurname")
            .createdSurveys(null)
            .build();

    private final SurveySenderDTO surveySenderDTO = new SurveySenderDTO(1L, "survey", "sur", "pass",
            0, 5, 0, user.getId(), user.getName(), user.getSurname(), false);

    private final SurveySenderDTO secondSurveySenderDTO = new SurveySenderDTO(2L, "secondSurv", "descp", "123",
            null, null, null, null, null, null, false);

    private final SurveyReceiverDTO surveyReceiverDTO = new SurveyReceiverDTO("newName", "newDesc",
            0, 5, 0, "new", user.getId());

    private final List<SurveySenderDTO> surveys = List.of(surveySenderDTO, secondSurveySenderDTO);

    private final String SURVEY_MAIN_PATH = Path.SURVEY_VALUE + "s";

    @Test
    void getSurveys_test() throws Exception {
        given(surveyService.getSurveys(1))
                .willReturn(new PageImpl<>(List.of(surveySenderDTO, secondSurveySenderDTO)));

        mockMvc.perform(get(SURVEY_MAIN_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPage", is(1), Integer.class))
                .andExpect(jsonPath("$.totalPages", is(1), Integer.class))
                .andExpect(jsonPath("$.surveys.[0].surveyId", is(surveySenderDTO.surveyId()), Long.class))
                .andExpect(jsonPath("$.surveys.[0].name", is(surveySenderDTO.name())))
                .andExpect(jsonPath("$.surveys.[0].description", is(surveySenderDTO.description())))
                .andExpect(jsonPath("$.surveys.[0].passcode", is(surveySenderDTO.passcode())))
                .andExpect(jsonPath("$.surveys.[0].authorId", is(surveySenderDTO.authorId()), Long.class))
                .andExpect(jsonPath("$.surveys.[0].authorName", is(surveySenderDTO.authorName())))
                .andExpect(jsonPath("$.surveys.[0].authorSurname", is(surveySenderDTO.authorSurname())));
    }

    @Test
    void getSurvey_by_id_test() throws Exception {
        given(surveyService.getSurvey(surveySenderDTO.surveyId())).willReturn(surveySenderDTO);

        mockMvc.perform(get(SURVEY_MAIN_PATH + "/{id}", surveySenderDTO.surveyId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.surveyId", is(surveySenderDTO.surveyId()), Long.class))
                .andExpect(jsonPath("$.name", is(surveySenderDTO.name())))
                .andExpect(jsonPath("$.description", is(surveySenderDTO.description())))
                .andExpect(jsonPath("$.passcode", is(surveySenderDTO.passcode())))
                .andExpect(jsonPath("$.authorId", is(surveySenderDTO.authorId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(surveySenderDTO.authorName())))
                .andExpect(jsonPath("$.authorSurname", is(surveySenderDTO.authorSurname())));
    }

    @Test
    void getSurveysByAuthorID_test() throws Exception {
        given(surveyService.getSurveysByAuthorID(user.getId(), 1))
                .willReturn(new PageImpl<>(List.of(surveySenderDTO, secondSurveySenderDTO)));

        mockMvc.perform(get(SURVEY_MAIN_PATH + Path.USER_VALUE + "/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPage", is(1), Integer.class))
                .andExpect(jsonPath("$.totalPages", is(1), Integer.class))
                .andExpect(jsonPath("$.surveys.[0].surveyId", is(surveySenderDTO.surveyId()), Long.class))
                .andExpect(jsonPath("$.surveys.[0].name", is(surveySenderDTO.name())))
                .andExpect(jsonPath("$.surveys.[0].description", is(surveySenderDTO.description())))
                .andExpect(jsonPath("$.surveys.[0].passcode", is(surveySenderDTO.passcode())))
                .andExpect(jsonPath("$.surveys.[0].authorId", is(surveySenderDTO.authorId()), Long.class))
                .andExpect(jsonPath("$.surveys.[0].authorName", is(surveySenderDTO.authorName())))
                .andExpect(jsonPath("$.surveys.[0].authorSurname", is(surveySenderDTO.authorSurname())));
    }

    @Test
    void deleteSurvey_test() throws Exception {
        mockMvc.perform(delete((SURVEY_MAIN_PATH + "/{id}"), surveySenderDTO.surveyId()))
                .andExpect(status().isOk());
    }

    @Test
    void updateSurvey_test() throws Exception {
        SurveySenderDTO newSurvey = new SurveySenderDTO(1L, surveyReceiverDTO.name(), surveyReceiverDTO.description(),
                surveyReceiverDTO.passcode(), 0, 6, 0, user.getId(), user.getName(), user.getSurname(), false);

        given(surveyService.updateSurvey(surveySenderDTO.surveyId(), surveyReceiverDTO)).willReturn(newSurvey);
        String content = new ObjectMapper().writeValueAsString(surveyReceiverDTO);

        mockMvc.perform(put(SURVEY_MAIN_PATH + "/{id}", surveySenderDTO.surveyId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.surveyId", is(newSurvey.surveyId()), Long.class))
                .andExpect(jsonPath("$.name", is(newSurvey.name())))
                .andExpect(jsonPath("$.description", is(newSurvey.description())))
                .andExpect(jsonPath("$.passcode", is(newSurvey.passcode())))
                .andExpect(jsonPath("$.authorId", is(newSurvey.authorId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(newSurvey.authorName())))
                .andExpect(jsonPath("$.authorSurname", is(newSurvey.authorSurname())));
    }

    @Test
    void addSurvey_test() throws Exception {
        given(surveyService.addSurvey(surveyReceiverDTO)).willReturn(secondSurveySenderDTO);

        String content = new ObjectMapper().writeValueAsString(surveyReceiverDTO);

        mockMvc.perform(post(SURVEY_MAIN_PATH + "")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.surveyId", is(secondSurveySenderDTO.surveyId()), Long.class))
                .andExpect(jsonPath("$.name", is(secondSurveySenderDTO.name())))
                .andExpect(jsonPath("$.description", is(secondSurveySenderDTO.description())))
                .andExpect(jsonPath("$.passcode", is(secondSurveySenderDTO.passcode())))
                .andExpect(jsonPath("$.authorId", is(secondSurveySenderDTO.authorId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(secondSurveySenderDTO.authorName())))
                .andExpect(jsonPath("$.authorSurname", is(secondSurveySenderDTO.authorSurname())));
    }

    @Test
    void getSurvey_status_not_found() throws Exception {
        NotFoundException notFoundException = new NotFoundException("Can't find data which was request");
        given(surveyService.getSurvey(surveySenderDTO.surveyId())).willThrow(notFoundException);

        mockMvc.perform(get(SURVEY_MAIN_PATH + "/{id}", surveySenderDTO.surveyId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("The requested data could not be found")));
    }

    @Test
    void startSurvey() throws Exception {
        doNothing().when(surveyService).startSurvey(surveySenderDTO.surveyId());

        mockMvc.perform(patch(SURVEY_MAIN_PATH + "/{id}/start", surveySenderDTO.surveyId()))
                .andExpect(status().isOk());
    }

    @Test
    void stopSurvey() throws Exception {
        doNothing().when(surveyService).stopSurvey(surveySenderDTO.surveyId());

        mockMvc.perform(patch(SURVEY_MAIN_PATH + "/{id}/stop", surveySenderDTO.surveyId()))
                .andExpect(status().isOk());
    }

    @Test
    void isStartedSurvey() throws Exception {
        given(surveyService.getSurveyStatus(surveySenderDTO.surveyId())).willReturn(new SurveyStatusDTO(true));
        mockMvc.perform(get(SURVEY_MAIN_PATH + "/{id}/status", surveySenderDTO.surveyId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status", is(true), Boolean.class))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
