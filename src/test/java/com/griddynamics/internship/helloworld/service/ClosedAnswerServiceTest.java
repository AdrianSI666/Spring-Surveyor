package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.ClosedAnswer;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.ClosedAnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ClosedAnswerSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.ClosedAnswerMapperImpl;
import com.griddynamics.internship.helloworld.repository.ClosedAnswerRepository;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.specyfication.ClosedAnswerSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.griddynamics.internship.helloworld.service.ClosedAnswerService.CLOSED_ANSWER_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClosedAnswerServiceTest {
    @Mock
    private ClosedAnswerRepository closedAnswerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Spy
    private ClosedAnswerMapperImpl mapper;
    @InjectMocks
    private ClosedAnswerService underTesting;

    private final Survey survey = Survey.builder()
            .id(5L)
            .name("survName")
            .description("testDesc")
            .passcode("pass")
            .duration(LocalTime.of(0, 5, 0))
            .questions(new ArrayList<>())
            .author(User.builder().name("userName").surname("userSur").build())
            .participants(new ArrayList<>())
            .started(true)
            .build();
    private final Question question = Question.builder()
            .id(5L)
            .name("QuestionMockTest")
            .content("Is it good?")
            .survey(survey)
            .answers(new ArrayList<>())
            .build();

    private final ClosedAnswer answer1 = ClosedAnswer.builder()
            .id(5L)
            .content("mockTestAnswer")
            .value(1)
            .question(question)
            .build();

    private final ClosedAnswer answer2 = ClosedAnswer.builder()
            .id(6L)
            .content("mockTestAnswer2")
            .value(0)
            .question(question)
            .build();

    @Test
    void getClosedAnswers() {
        given(closedAnswerRepository.findAll((Sort) any())).willReturn(List.of(answer1, answer2));
        List<ClosedAnswer> answerList = List.of(answer1, answer2);
        List<ClosedAnswerSenderDTO> answerSenderDTOS = answerList
                .stream()
                .map(mapper::map)
                .toList();
        assertEquals(answerSenderDTOS, underTesting.getClosedAnswers());
    }

    @Test
    void getClosedAnswersReturnsEmptyList() {
        given(closedAnswerRepository.findAll((Sort) any())).willReturn(List.of());
        assertEquals(List.of(), underTesting.getClosedAnswers());
    }

    @Test
    void addClosedAnswer() {
        Long questionId = answer1.getQuestion().getId();
        ClosedAnswerReceiverDTO closedAnswerReceiverDTO = new ClosedAnswerReceiverDTO(answer1.getContent(),
                answer1.getValue(),
                questionId);
        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        given(closedAnswerRepository.save(any())).willReturn(answer1);
        underTesting.addClosedAnswer(closedAnswerReceiverDTO);

        ArgumentCaptor<ClosedAnswer> argumentCaptor = ArgumentCaptor.forClass(ClosedAnswer.class);
        verify(closedAnswerRepository).save(argumentCaptor.capture());
        ClosedAnswer capturedParticipant = argumentCaptor.getValue();

        assertEquals(capturedParticipant.getContent(), answer1.getContent());
        assertEquals(capturedParticipant.getValue(), answer1.getValue());
        assertEquals(capturedParticipant.getQuestion(), answer1.getQuestion());
    }

    @Test
    void addClosedAnswerThrowsNotFoundWhenQuestionIsMissing() {
        final String QUESTION_NOT_FOUND_MSG = "Couldn't find question with id: %d";
        Long questionId = answer1.getQuestion().getId();
        ClosedAnswerReceiverDTO closedAnswerReceiverDTO = new ClosedAnswerReceiverDTO(answer1.getContent(),
                answer1.getValue(),
                questionId);
        given(questionRepository.findById(questionId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.addClosedAnswer(closedAnswerReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(QUESTION_NOT_FOUND_MSG, questionId));
        verify(closedAnswerRepository, times(0)).save(any());
    }

    @Test
    void updateClosedAnswer() {
        Long answerId = answer1.getId();
        ClosedAnswerReceiverDTO closedAnswerReceiverDTO = new ClosedAnswerReceiverDTO(answer2.getContent(),
                answer2.getValue(),
                null);
        given(closedAnswerRepository.findById(answerId)).willReturn(Optional.of(answer1));
        answer2.setId(answerId);
        given(closedAnswerRepository.save(any())).willReturn(answer2);
        ClosedAnswerSenderDTO closedAnswerSenderDTO = new ClosedAnswerSenderDTO(answerId,
                answer2.getContent(),
                answer2.getValue(),
                answer2.getQuestion().getId());
        assertEquals(closedAnswerSenderDTO, underTesting.updateClosedAnswer(closedAnswerReceiverDTO, answerId));
    }

    @Test
    void updateClosedAnswerThrowsClosedAnswerNotFound() {
        Long answerId = answer1.getId();
        ClosedAnswerReceiverDTO closedAnswerReceiverDTO = new ClosedAnswerReceiverDTO(answer2.getContent(),
                answer2.getValue(),
                null);
        given(closedAnswerRepository.findById(answerId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.updateClosedAnswer(closedAnswerReceiverDTO, answerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(CLOSED_ANSWER_NOT_FOUND_MSG, answerId));
        verify(closedAnswerRepository, times(0)).save(any());
    }

    @Test
    void deleteClosedAnswer() {
        Long answerId = answer1.getId();
        given(closedAnswerRepository.findById(answerId)).willReturn(Optional.of(answer1));
        underTesting.deleteClosedAnswer(answerId);
        then(closedAnswerRepository).should().delete(answer1);
    }

    @Test
    void deleteClosedAnswerThrowsClosedAnswerNotFound() {
        Long answerId = answer1.getId();
        given(closedAnswerRepository.findById(answerId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.deleteClosedAnswer(answerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(CLOSED_ANSWER_NOT_FOUND_MSG, answerId));
    }

    @Test
    void getClosedAnswerById() {
        Long answerId = answer1.getId();
        given(closedAnswerRepository.findById(answerId)).willReturn(Optional.of(answer1));
        assertEquals(mapper.map(answer1), underTesting.getClosedAnswerById(answerId));
    }

    @Test
    void getClosedAnswerByIdThrowsClosedAnswerNotFound() {
        Long answerId = answer1.getId();
        given(closedAnswerRepository.findById(answerId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.getClosedAnswerById(answerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(CLOSED_ANSWER_NOT_FOUND_MSG, answerId));
    }

    @Test
    void getClosedAnswerByQuestionId() {
        given(closedAnswerRepository.findAll((ClosedAnswerSpecification) any())).willReturn(List.of(answer1, answer2));
        assertEquals(Stream.of(answer1, answer2).map(mapper::map).toList(),
                underTesting.getClosedAnswerByQuestionId(question.getId()));
    }

    @Test
    void getClosedAnswerByQuestionIdReturnsEmptyList() {
        given(closedAnswerRepository.findAll((ClosedAnswerSpecification) any())).willReturn(List.of());
        assertEquals(List.of(), underTesting.getClosedAnswerByQuestionId(question.getId()));
    }
}