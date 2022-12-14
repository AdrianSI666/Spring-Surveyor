package com.griddynamics.internship.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.dto.receiver.QuestionReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.QuestionSenderDTO;
import com.griddynamics.internship.helloworld.enums.ContentType;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.QuestionService;
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
class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionControllerUnderTesting;
    private final QuestionSenderDTO question1 = new QuestionSenderDTO(
            5L,
            "testNameDTO1",
            "testContentDTO1",
            0,
            5,
            1,
            5L
    );
    private final QuestionSenderDTO question2 = new QuestionSenderDTO(
            6L,
            "testNameDTO2",
            "testContentDTO2",
            0,
            45,
            1,
            5L
    );

    private final QuestionReceiverDTO questionReceiver1 = new QuestionReceiverDTO(
            "testNameDTO1",
            "testContentDTO1",
            5L
    );

    private final String QUESTION_MAIN_PATH = Path.QUESTION_VALUE + "s";

    @Test
    void getQuestion() throws Exception {
        Long questionId = question1.id();
        given(questionService.getQuestion(questionId)).willReturn(question1);
        String sender = (new ObjectMapper()).writeValueAsString(question1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(QUESTION_MAIN_PATH + "/{id}", questionId);
        MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getQuestions() throws Exception {
        given(questionService.getQuestions(1)).willReturn(new PageImpl<>(List.of(question1, question2)));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(QUESTION_MAIN_PATH + "");
        MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(jsonPath("$.currentPage", is(1), Integer.class))
                .andExpect(jsonPath("$.totalPages", is(1), Integer.class))
                .andExpect(jsonPath("$.questions.[0].id", is(question1.id()), Long.class))
                .andExpect(jsonPath("$.questions.[0].name", is(question1.name())))
                .andExpect(jsonPath("$.questions.[0].content", is(question1.content())))
                .andExpect(jsonPath("$.questions.[0].hours", is(question1.hours())))
                .andExpect(jsonPath("$.questions.[0].minutes", is(question1.minutes())))
                .andExpect(jsonPath("$.questions.[0].seconds", is(question1.seconds())))
                .andExpect(jsonPath("$.questions.[0].surveyId", is(question1.surveyId()), Long.class));
    }

    @Test
    void addQuestion() throws Exception {
        given(questionService.addQuestion(questionReceiver1)).willReturn(question1);
        String content = (new ObjectMapper()).writeValueAsString(questionReceiver1);
        String sender = (new ObjectMapper()).writeValueAsString(question1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(QUESTION_MAIN_PATH + "")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content()
                        .string(sender));
    }

    @Test
    void updateQuestion() throws Exception {
        Long id = question1.id();
        given(questionService.updateQuestion(id, questionReceiver1)).willReturn(question1);
        String content = (new ObjectMapper()).writeValueAsString(questionReceiver1);
        String sender = (new ObjectMapper()).writeValueAsString(question1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(QUESTION_MAIN_PATH + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content);
        MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void deleteQuestion() throws Exception {
        Long id = question1.id();
        doNothing().when(questionService).deleteQuestion(id);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(QUESTION_MAIN_PATH + "/{id}", id);
        MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getQuestionsBySurvey() throws Exception {
        Long id = 5L;
        given(questionService.getQuestionsBySurvey(id)).willReturn(List.of(question1, question2));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(question1, question2));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(QUESTION_MAIN_PATH + Path.SURVEY_VALUE + "/{id}", id);
        MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getNextQuestionInSurvey() throws Exception {
        Long id = 5L;
        given(questionService.getNextQuestionInSurvey(id)).willReturn(question1);
        String sender = (new ObjectMapper()).writeValueAsString(question1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(QUESTION_MAIN_PATH + Path.SURVEY_VALUE + "/{id}/next", id);
        MockMvcBuilders.standaloneSetup(questionControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }
}