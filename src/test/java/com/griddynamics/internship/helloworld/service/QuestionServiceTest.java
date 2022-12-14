package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.QuestionReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.QuestionSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.QuestionMapperImpl;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.specification.QuestionSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private SurveyRepository surveyRepository;
    @Spy
    private QuestionMapperImpl mapper;
    @Mock
    private Clock clock;
    private static final ZonedDateTime NOW = ZonedDateTime.of(
            2022,
            11,
            7,
            12,
            30,
            30,
            0,
            ZoneId.of("UTC")
    );
    @InjectMocks
    private QuestionService underTesting;
    private final Survey survey = Survey.builder()
            .id(5L)
            .name("SurveyTest")
            .description("survDesc")
            .passcode("pass")
            .duration(LocalTime.of(0, 5, 0))
            .questions(new ArrayList<>())
            .author(User.builder().name("userName").surname("userSur").build())
            .participants(null)
            .started(false)
            .build();

    private final Question question = Question.builder()
            .id(5L)
            .name("testingQuestion")
            .content("Is this working?")
            .answers(null)
            .survey(survey)
            .build();

    private final Question question2 = Question.builder()
            .id(6L)
            .name("testingQuestion2")
            .content("And is this working?")
            .answers(null)
            .survey(survey)
            .build();

    @Test
    void getQuestions() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize, Sort.by("id"));
        Page<Question> questionPage = new PageImpl<>(List.of(question, question2), paging, 2);
        given(questionRepository.findAll(paging)).willReturn(questionPage);

        Page<QuestionSenderDTO> questionSenderDTOSFromService = underTesting.getQuestions(1);
        Page<QuestionSenderDTO> questionSenderDTOS = questionPage.map(mapper::map);

        assertEquals(questionSenderDTOS, questionSenderDTOSFromService);
    }

    @Test
    void getQuestionsReturnEmptyList() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize, Sort.by("id"));
        Page<Question> questionPage = new PageImpl<>(List.of(), paging, 0);
        given(questionRepository.findAll(paging)).willReturn(questionPage);

        Page<QuestionSenderDTO> questionSenderDTOSFromService = underTesting.getQuestions(1);
        Page<QuestionSenderDTO> questionSenderDTOS = questionPage.map(mapper::map);

        assertEquals(questionSenderDTOS, questionSenderDTOSFromService);
    }

    @Test
    void getQuestion() {
        given(questionRepository.findById(anyLong())).willReturn(Optional.of(question));
        assertEquals(mapper.map(question), underTesting.getQuestion(question.getId()));
    }

    @Test
    void getQuestionThrowsExceptionWhenEmpty() {
        given(questionRepository.findById(anyLong())).willReturn(Optional.empty());
        Long id = question.getId();
        assertThatThrownBy(() -> underTesting.getQuestion(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Couldn't find question with id: %d", id));
    }

    @Test
    void addQuestion() {

        QuestionReceiverDTO questionReceiverDTO = new QuestionReceiverDTO(question.getName(),
                question.getContent(),
                question.getSurvey().getId());
        given(surveyRepository.findById(question.getSurvey().getId())).willReturn(Optional.of(survey));
        given(questionRepository.save(any())).willReturn(question);
        underTesting.addQuestion(questionReceiverDTO);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).save(argumentCaptor.capture());
        Question capturedQuestion = argumentCaptor.getValue();

        assertEquals(capturedQuestion.getName(), questionReceiverDTO.name());
        assertEquals(capturedQuestion.getContent(), questionReceiverDTO.content());

        assertEquals(capturedQuestion.getSurvey().getId(), questionReceiverDTO.surveyId());
    }

    @Test
    void addQuestionWithoutSurveyThrowException() {

        QuestionReceiverDTO questionReceiverDTO = new QuestionReceiverDTO(question.getName(),
                question.getContent(),
                question.getSurvey().getId());
        given(surveyRepository.findById(question.getSurvey().getId())).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.addQuestion(questionReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Couldn't find survey with id: %d", question.getSurvey().getId()));
    }

    @Test
    void updateQuestion() {
        QuestionReceiverDTO questionReceiverDTO = new QuestionReceiverDTO(question.getName(),
                question.getContent(),
                question.getSurvey().getId());
        given(questionRepository.findById(anyLong())).willReturn(Optional.of(question));
        given(questionRepository.save(any())).willReturn(question);
        underTesting.updateQuestion(question.getId(), questionReceiverDTO);
        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).save(argumentCaptor.capture());
        Question capturedQuestion = argumentCaptor.getValue();
        assertEquals(capturedQuestion.getName(), questionReceiverDTO.name());
        assertEquals(capturedQuestion.getContent(), questionReceiverDTO.content());
    }

    @Test
    void updateQuestionNotFoundQuestion() {
        QuestionReceiverDTO questionReceiverDTO = new QuestionReceiverDTO(question.getName(),
                question.getContent(),
                question.getSurvey().getId());
        given(questionRepository.findById(anyLong())).willReturn(Optional.empty());
        Long id = question.getId();
        assertThatThrownBy(() -> underTesting.updateQuestion(id, questionReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Couldn't find question with id: %d", question.getId()));
    }

    @Test
    void deleteQuestion() {
        given(questionRepository.findById(anyLong())).willReturn(Optional.of(question));
        underTesting.deleteQuestion(question.getId());
        then(questionRepository).should().delete(question);
    }

    @Test
    void deleteQuestionNotFoundQuestion() {
        given(questionRepository.findById(5L)).willReturn(Optional.empty());
        Long id = question.getId();
        assertThatThrownBy(() -> underTesting.deleteQuestion(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Couldn't find question with id: %d", question.getId()));
    }

    @Test
    void getQuestionsBySurvey() {
        Long id = question.getSurvey().getId();
        survey.setStarted(true);
        given(questionRepository.findAll(any(QuestionSpecification.class))).willReturn(List.of(question, question2));
        List<QuestionSenderDTO> questionSenderDTOList = new ArrayList<>();
        questionSenderDTOList.add(mapper.map(question));
        questionSenderDTOList.add(mapper.map(question2));
        assertEquals(questionSenderDTOList, underTesting.getQuestionsBySurvey(id));
    }

    @Test
    void getQuestionsBySurveyReturnedEmptyList() {
        Long id = question.getSurvey().getId();
        survey.setStarted(true);
        given(questionRepository.findAll(any(QuestionSpecification.class))).willReturn(List.of());
        List<QuestionSenderDTO> questionSenderDTOList = new ArrayList<>();
        assertEquals(questionSenderDTOList, underTesting.getQuestionsBySurvey(id));
    }

    @Test
    void getNextQuestionInSurvey() {
        given(clock.instant()).willReturn(NOW.toInstant());
        Survey survey1 = Survey.builder()
                .id(10L)
                .name("SurveyTimeTest")
                .description("survDesc")
                .passcode("pass")
                .duration(LocalTime.of(0, 5, 0))
                .questions(List.of(question, question2))
                .author(User.builder().name("userName").surname("userSur").build())
                .participants(null)
                .started(true)
                .dateTimeStarted(clock.instant().minusSeconds(30))
                .build();
        Long id = survey1.getId();
        given(surveyRepository.findById(id)).willReturn(Optional.of(survey1));
        given(questionRepository.findAll(any(QuestionSpecification.class))).willReturn(List.of(question, question2));
        assertEquals(mapper.map(question), underTesting.getNextQuestionInSurvey(id));
    }

    @Test
    void getNextQuestionInSurveyThrowsSurveyAlreadyEndedException() {
        given(clock.instant()).willReturn(NOW.toInstant());
        Survey survey1 = Survey.builder()
                .id(10L)
                .name("SurveyTimeTest")
                .description("survDesc")
                .passcode("pass")
                .duration(LocalTime.of(0, 5, 0))
                .questions(List.of(question, question2))
                .author(User.builder().name("userName").surname("userSur").build())
                .participants(null)
                .started(true)
                .dateTimeStarted(clock.instant().minus(10, ChronoUnit.MINUTES))
                .build();
        Long id = survey1.getId();
        given(surveyRepository.findById(id)).willReturn(Optional.of(survey1));
        given(questionRepository.findAll(any(QuestionSpecification.class))).willReturn(List.of(question, question2));
        assertThatThrownBy(() -> underTesting.getNextQuestionInSurvey(id))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("Can't get any more questions, survey was already ended.");
    }

    @Test
    void getNextQuestionInSurveyThrowsSurveyNotStartedException() {
        Survey survey1 = Survey.builder()
                .id(10L)
                .name("SurveyTimeTest")
                .description("survDesc")
                .passcode("pass")
                .duration(LocalTime.of(0, 5, 0))
                .questions(List.of(question, question2))
                .author(User.builder().name("userName").surname("userSur").build())
                .participants(null)
                .started(false)
                .build();
        Long id = survey1.getId();
        given(surveyRepository.findById(id)).willReturn(Optional.of(survey1));
        assertThatThrownBy(() -> underTesting.getNextQuestionInSurvey(id))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("Can't get questions, because survey did not start yet");
    }
}