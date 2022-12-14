package com.griddynamics.internship.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.internship.helloworld.domain.AnswerID;
import com.griddynamics.internship.helloworld.dto.receiver.AnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.AnswerSenderDTO;
import com.griddynamics.internship.helloworld.enums.ContentType;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.AnswerService;
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
class AnswerControllerTest {
    @Mock
    private AnswerService answerService;

    @InjectMocks
    private AnswerController answerControllerUnderTesting;
    private final AnswerSenderDTO answer1 = new AnswerSenderDTO(
            "answerContent1",
            5L,
            "questionName",
            "questionContext",
            5L,
            "participantNickname",
            null,
            null
    );
    private final AnswerSenderDTO answer2 = new AnswerSenderDTO(
            "answerContent2",
            6L,
            "questionName6",
            "questionContext6",
            5L,
            "participantNickname",
            null,
            null
    );

    private final AnswerReceiverDTO answerReceiver1 = new AnswerReceiverDTO(
            "answerContent1",
            5L,
            5L,
            null
    );

    private final String ANSWER_MAIN_PATH = Path.ANSWER_VALUE + "s";

    @Test
    void getAnswersByQuestionAndByParticipant() throws Exception {
        AnswerID answerID = new AnswerID(5L, 5L);
        given(answerService.getAnswerByQuestionIdAndParticipantId(answerID.getQuestionID(), answerID.getParticipantID())).willReturn(answer1);
        String sender = (new ObjectMapper()).writeValueAsString(answer1);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(ANSWER_MAIN_PATH + Path.QUESTION_VALUE + "/{questionID}" + Path.PARTICIPANT_VALUE + "/{participantId}",
                        answerID.getQuestionID(),
                        answerID.getParticipantID());
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getAnswers() throws Exception {
        given(answerService.getAnswers(1)).willReturn(new PageImpl<>(List.of(answer1, answer2)));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(ANSWER_MAIN_PATH);
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(jsonPath("$.currentPage", is(1), Integer.class))
                .andExpect(jsonPath("$.totalPages", is(1), Integer.class))
                .andExpect(jsonPath("$.answers.[0].participant_id", is(answer1.participant_id()), Long.class))
                .andExpect(jsonPath("$.answers.[0].question_id", is(answer1.question_id()), Long.class))
                .andExpect(jsonPath("$.answers.[0].content", is(answer1.content())))
                .andExpect(jsonPath("$.answers.[0].participant_nickname", is(answer1.participant_nickname())))
                .andExpect(jsonPath("$.answers.[0].question_name", is(answer1.question_name())))
                .andExpect(jsonPath("$.answers.[0].question_context", is(answer1.question_context())));
    }

    @Test
    void addAnswer() throws Exception {
        given(answerService.addAnswer(answerReceiver1)).willReturn(answer1);
        String content = (new ObjectMapper()).writeValueAsString(answerReceiver1);
        String sender = (new ObjectMapper()).writeValueAsString(answer1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(ANSWER_MAIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content()
                        .string(sender));
    }

    @Test
    void updateAnswer() throws Exception {
        given(answerService.updateAnswer(answerReceiver1)).willReturn(answer1);
        String content = (new ObjectMapper()).writeValueAsString(answerReceiver1);
        String sender = (new ObjectMapper()).writeValueAsString(answer1);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(ANSWER_MAIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void deleteAnswer() throws Exception {
        AnswerID answerID = new AnswerID(5L, 5L);
        doNothing().when(answerService).deleteAnswer(answerID.getQuestionID(), answerID.getParticipantID());
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete(ANSWER_MAIN_PATH + Path.QUESTION_VALUE + "/{questionID}" + Path.PARTICIPANT_VALUE + "/{participantId}",
                        answerID.getQuestionID(),
                        answerID.getParticipantID());
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAnswersByQuestion() throws Exception {
        Long questionId = answer1.question_id();
        given(answerService.getAnswersByQuestion(questionId)).willReturn(List.of(answer1));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(answer1));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(ANSWER_MAIN_PATH + Path.QUESTION_VALUE + "/{questionID}", questionId);
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getAnswersByParticipant() throws Exception {
        Long participantId = answer1.participant_id();
        given(answerService.getAnswersByParticipant(participantId)).willReturn(List.of(answer1, answer2));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(answer1, answer2));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(ANSWER_MAIN_PATH + Path.PARTICIPANT_VALUE + "/{participantID}", participantId);
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }

    @Test
    void getAnswersBySurvey() throws Exception {
        Long surveyId = 5L;
        given(answerService.getAnswersBySurvey(surveyId)).willReturn(List.of(answer1, answer2));
        String sender = (new ObjectMapper()).writeValueAsString(List.of(answer1, answer2));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(ANSWER_MAIN_PATH + Path.SURVEY_VALUE + "/{surveyID}", surveyId);
        MockMvcBuilders.standaloneSetup(answerControllerUnderTesting)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(ContentType.JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(sender));
    }
}