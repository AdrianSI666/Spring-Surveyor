package com.griddynamics.internship.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.dto.receiver.ClosedAnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ClosedAnswerSenderDTO;
import com.griddynamics.internship.helloworld.enums.ContentType;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.ClosedAnswerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ClosedAnswerControllerTest {
    @Mock
    private ClosedAnswerService closedAnswerService;

    @InjectMocks
    private ClosedAnswerController closedAnswerControllerUnderTesting;

    private final ClosedAnswerSenderDTO closedAnswerSenderDTO1 = new ClosedAnswerSenderDTO(
            5L,
            "answerContentCorrect",
            1,
            5L
    );
    private final ClosedAnswerSenderDTO closedAnswerSenderDTO2 = new ClosedAnswerSenderDTO(
            6L,
            "answerContentBad",
            0,
            5L
    );

    private final ClosedAnswerReceiverDTO closedAnswerReceiverDTO = new ClosedAnswerReceiverDTO(
            "answerContent",
            1,
            5L
    );

    private final String CLOSED_ANSWER_MAIN_PATH = Path.CLOSED_ANSWER_VALUE + "s";

    @Test
    void getClosedAnswers() throws Exception {
        given(closedAnswerService.getClosedAnswers()).willReturn(List.of(closedAnswerSenderDTO1,
                closedAnswerSenderDTO2));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(closedAnswerSenderDTO1,
                closedAnswerSenderDTO2));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(CLOSED_ANSWER_MAIN_PATH);
        MockMvcBuilders.standaloneSetup(closedAnswerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getClosedAnswersById() throws Exception {
        given(closedAnswerService.getClosedAnswerById(closedAnswerSenderDTO1.id()))
                .willReturn(closedAnswerSenderDTO1);
        String sender = (new ObjectMapper()).writeValueAsString(closedAnswerSenderDTO1);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(CLOSED_ANSWER_MAIN_PATH + "/%d".formatted(closedAnswerSenderDTO1.id()));
        MockMvcBuilders.standaloneSetup(closedAnswerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void addClosedAnswer() throws Exception {
        given(closedAnswerService.addClosedAnswer(closedAnswerReceiverDTO))
                .willReturn(closedAnswerSenderDTO1);
        String content = (new ObjectMapper()).writeValueAsString(closedAnswerReceiverDTO);
        String sender = (new ObjectMapper()).writeValueAsString(closedAnswerSenderDTO1);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(CLOSED_ANSWER_MAIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);
        MockMvcBuilders.standaloneSetup(closedAnswerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void updateClosedAnswer() throws Exception {
        given(closedAnswerService.updateClosedAnswer(closedAnswerReceiverDTO, closedAnswerSenderDTO1.id()))
                .willReturn(closedAnswerSenderDTO2);
        String content = (new ObjectMapper()).writeValueAsString(closedAnswerReceiverDTO);
        String sender = (new ObjectMapper()).writeValueAsString(closedAnswerSenderDTO2);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put(CLOSED_ANSWER_MAIN_PATH + "/{id}", closedAnswerSenderDTO1.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);
        MockMvcBuilders.standaloneSetup(closedAnswerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void deleteClosedAnswer() throws Exception {
        doNothing().when(closedAnswerService).deleteClosedAnswer(closedAnswerSenderDTO1.id());
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete(CLOSED_ANSWER_MAIN_PATH + "/{id}", closedAnswerSenderDTO1.id());
        MockMvcBuilders.standaloneSetup(closedAnswerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getClosedAnswersByQuestion() throws Exception {
        given(closedAnswerService.getClosedAnswerByQuestionId(closedAnswerReceiverDTO.questionId()))
                .willReturn(List.of(closedAnswerSenderDTO1, closedAnswerSenderDTO2));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(closedAnswerSenderDTO1,
                closedAnswerSenderDTO2));
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(CLOSED_ANSWER_MAIN_PATH + Path.QUESTION_VALUE + "/{id}",
                        closedAnswerReceiverDTO.questionId());
        MockMvcBuilders.standaloneSetup(closedAnswerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }
}