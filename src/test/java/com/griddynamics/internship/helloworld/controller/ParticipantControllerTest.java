package com.griddynamics.internship.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.dto.receiver.ParticipantReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ParticipantSenderDTO;
import com.griddynamics.internship.helloworld.enums.ContentType;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.ParticipantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private ParticipantController participantControllerUnderTesting;
    private final ParticipantSenderDTO participant1 = new ParticipantSenderDTO(
            5L,
            "testNicknameDTO1",
            5L
    );
    private final ParticipantSenderDTO participant2 = new ParticipantSenderDTO(
            6L,
            "testNicknameDTO2",
            5L
    );

    private final ParticipantReceiverDTO participantReceiver1 = new ParticipantReceiverDTO(
            "testNameDTO1",
            "passcode"
    );

    private final String PARTICIPANT_MAIN_PATH = Path.PARTICIPANT_VALUE + "s";

    @Test
    void getParticipant() throws Exception {
        Long ParticipantId = participant1.id();
        given(participantService.getParticipantById(ParticipantId)).willReturn(participant1);
        String sender = (new ObjectMapper()).writeValueAsString(participant1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANT_MAIN_PATH + "/{id}", ParticipantId);
        MockMvcBuilders.standaloneSetup(participantControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getParticipants() throws Exception {
        given(participantService.getParticipants(1)).willReturn(new PageImpl<>(List.of(participant1, participant2)));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANT_MAIN_PATH);
        MockMvcBuilders.standaloneSetup(participantControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(jsonPath("$.currentPage", is(1), Integer.class))
                .andExpect(jsonPath("$.totalPages", is(1), Integer.class))
                .andExpect(jsonPath("$.participants.[0].id", is(participant1.id()), Long.class))
                .andExpect(jsonPath("$.participants.[0].nick", is(participant1.nick())))
                .andExpect(jsonPath("$.participants.[0].surveyId", is(participant1.surveyId()), Long.class));
    }

    @Test
    void addParticipant() throws Exception {
        given(participantService.addParticipant(participantReceiver1)).willReturn(participant1);
        String content = (new ObjectMapper()).writeValueAsString(participantReceiver1);
        String sender = (new ObjectMapper()).writeValueAsString(participant1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(PARTICIPANT_MAIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(participantControllerUnderTesting)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content()
                        .string(sender));
    }

    @Test
    void updateParticipant() throws Exception {
        Long id = participant1.id();
        given(participantService.updateParticipant(id, participantReceiver1)).willReturn(participant1);
        String content = (new ObjectMapper()).writeValueAsString(participantReceiver1);
        String sender = (new ObjectMapper()).writeValueAsString(participant1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(PARTICIPANT_MAIN_PATH + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(participantControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void deleteParticipant() throws Exception {
        Long id = participant1.id();
        doNothing().when(participantService).deleteParticipant(id);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(PARTICIPANT_MAIN_PATH + "/{id}", id);
        MockMvcBuilders.standaloneSetup(participantControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getParticipantsBySurveyId() throws Exception {
        Long id = 5L;
        given(participantService.getParticipantsBySurvey(id)).willReturn(List.of(participant1, participant2));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(participant1, participant2));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(PARTICIPANT_MAIN_PATH + Path.SURVEY_VALUE + "/{id}", id);
        MockMvcBuilders.standaloneSetup(participantControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }
}