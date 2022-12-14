package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.AnswerID;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.AnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.AnswerSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.AnswerMapperImpl;
import com.griddynamics.internship.helloworld.repository.AnswerRepository;
import com.griddynamics.internship.helloworld.repository.ParticipantRepository;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.specification.AnswerSpecification;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.griddynamics.internship.helloworld.service.AnswerService.ANSWER_NOT_FOUND_MSG;
import static com.griddynamics.internship.helloworld.service.ParticipantService.PARTICIPANT_NOT_FOUND_MSG;
import static com.griddynamics.internship.helloworld.service.QuestionService.QUESTION_NOT_FOUND_MSG;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Spy
    private AnswerMapperImpl mapper;
    @InjectMocks
    private AnswerService underTesting;

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

    private final Participant participant1 = Participant.builder()
            .id(5L)
            .nick("mockTestNick")
            .survey(survey)
            .build();

    private final Participant participant2 = Participant.builder()
            .id(6L)
            .nick("mockTestNickSecond")
            .survey(survey)
            .build();

    private final Question question = Question.builder()
            .id(5L)
            .name("QuestionMockTest")
            .content("Is it good?")
            .survey(survey)
            .answers(new ArrayList<>())
            .build();

    private final Answer answer1 = Answer.builder()
            .answerID(AnswerID.builder().questionID(5L).participantID(5L).build())
            .content("mockTestNick")
            .question(question)
            .participant(participant1)
            .build();

    private final Answer answer2 = Answer.builder()
            .answerID(AnswerID.builder().questionID(5L).participantID(6L).build())
            .content("mockTestNickSecond")
            .question(question)
            .participant(participant2)
            .build();
    private final AnswerReceiverDTO answerReceiverDTO = new AnswerReceiverDTO(answer1.getContent(),
            question.getId(),
            participant1.getId(),
            null);

    @Test
    void getAnswers() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize, Sort.by("questionId"));
        Page<Answer> answerPage = new PageImpl<>(List.of(answer1, answer2), paging, 0);
        given(answerRepository.findAll(paging)).willReturn(answerPage);

        Page<AnswerSenderDTO> answerSenderDTOSPageFromService = underTesting.getAnswers(1);
        Page<AnswerSenderDTO> answerSenderDTOS = answerPage.map(mapper::map);

        assertEquals(answerSenderDTOS, answerSenderDTOSPageFromService);
    }

    @Test
    void getAnswersEmptyList() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize, Sort.by("questionId"));
        Page<Answer> answerPage = new PageImpl<>(List.of(), paging, 0);
        given(answerRepository.findAll(paging)).willReturn(answerPage);

        Page<AnswerSenderDTO> answerSenderDTOSPageFromService = underTesting.getAnswers(1);
        Page<AnswerSenderDTO> answerSenderDTOS = answerPage.map(mapper::map);

        assertEquals(answerSenderDTOS, answerSenderDTOSPageFromService);
    }

    @Test
    void addAnswer() {
        Long questionId = question.getId();
        Long participantId = participant1.getId();

        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        given(participantRepository.findById(participantId)).willReturn(Optional.of(participant1));
        given(answerRepository.save(any())).willReturn(answer1);
        underTesting.addAnswer(answerReceiverDTO);

        ArgumentCaptor<Answer> argumentCaptor = ArgumentCaptor.forClass(Answer.class);
        verify(answerRepository).save(argumentCaptor.capture());
        Answer capturedAnswer = argumentCaptor.getValue();

        assertEquals(capturedAnswer.getContent(), answer1.getContent());
        assertEquals(capturedAnswer.getQuestion(), answer1.getQuestion());
        assertEquals(capturedAnswer.getParticipant(), answer1.getParticipant());
    }

    @Test
    void addAnswerThrowQuestionNotFoundException() {
        Long questionId = question.getId();
        given(questionRepository.findById(questionId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.addAnswer(answerReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(QUESTION_NOT_FOUND_MSG, questionId));
        verify(answerRepository, times(0)).save(any());
    }

    @Test
    void addAnswerThrowParticipantNotFoundException() {
        Long questionId = question.getId();
        Long participantId = participant1.getId();
        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        given(participantRepository.findById(participantId)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.addAnswer(answerReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(PARTICIPANT_NOT_FOUND_MSG, participantId));
    }

    @Test
    void updateAnswer() {
        AnswerID answerID = answer1.getAnswerID();
        String answer = "UpdateAnswer";
        given(answerRepository.findOne(any(Specification.class)))
                .willReturn(Optional.of(answer1));
        AnswerReceiverDTO AnswerReceiverDTO = new AnswerReceiverDTO(answer,
                answerID.getQuestionID(),
                answerID.getParticipantID(),
                null);
        given(answerRepository.save(any())).willReturn(answer2);
        underTesting.updateAnswer(AnswerReceiverDTO);

        ArgumentCaptor<Answer> argumentCaptor = ArgumentCaptor.forClass(Answer.class);
        verify(answerRepository).save(argumentCaptor.capture());
        Answer capturedAnswer = argumentCaptor.getValue();

        assertEquals(capturedAnswer.getContent(), answer);
        assertEquals(capturedAnswer.getAnswerID(), answerID);
    }

    @Test
    void updateAnswerThrowsNotFoundAnswer() {
        AnswerID answerID = answer1.getAnswerID();
        given(answerRepository.findOne(any(Specification.class)))
                .willReturn(Optional.empty());
        AnswerReceiverDTO AnswerReceiverDTO = new AnswerReceiverDTO("ignored",
                answerID.getQuestionID(),
                answerID.getParticipantID(),
                null);
        assertThatThrownBy(() -> underTesting.updateAnswer(AnswerReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(ANSWER_NOT_FOUND_MSG,
                        answerID.getParticipantID(),
                        answerID.getQuestionID()));
    }

    @Test
    void deleteAnswer() {
        given(answerRepository.findOne(any(Specification.class))).willReturn(Optional.of(answer1));
        underTesting.deleteAnswer(answer1.getAnswerID().getQuestionID(), answer1.getAnswerID().getParticipantID());
        then(answerRepository).should().delete(answer1);
    }

    @Test
    void deleteAnswerThrowAnswerNotFound() {
        Long questionId = answer1.getAnswerID().getQuestionID();
        Long participantId = answer1.getAnswerID().getParticipantID();
        given(answerRepository.findOne(any(Specification.class))).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.deleteAnswer(questionId, participantId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(ANSWER_NOT_FOUND_MSG, questionId, participantId));
    }

    @Test
    void getAnswerByQuestionIdAndParticipantId() {
        AnswerID answerID = answer1.getAnswerID();
        given(answerRepository.findOne(any(Specification.class)))
                .willReturn(Optional.of(answer1));
        assertEquals(mapper.map(answer1), underTesting.getAnswerByQuestionIdAndParticipantId(answerID.getQuestionID(),
                answerID.getParticipantID()));
    }

    @Test
    void getAnswerByQuestionIdAndParticipantIdThrowsNotFoundException() {
        Long questionId = answer1.getAnswerID().getQuestionID();
        Long participantId = answer1.getAnswerID().getParticipantID();
        given(answerRepository.findOne(any(Specification.class))).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.getAnswerByQuestionIdAndParticipantId(questionId, participantId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(ANSWER_NOT_FOUND_MSG, questionId, participantId));
    }

    @Test
    void getAnswersByQuestion() {
        Long id = answer1.getAnswerID().getQuestionID();
        given(answerRepository.findAll(any(AnswerSpecification.class))).willReturn(List.of(answer1, answer2));
        List<AnswerSenderDTO> AnswerSenderDTOS = new ArrayList<>();
        AnswerSenderDTOS.add(mapper.map(answer1));
        AnswerSenderDTOS.add(mapper.map(answer2));
        assertEquals(AnswerSenderDTOS, underTesting.getAnswersByQuestion(id));
    }

    @Test
    void getAnswersByQuestionEmptyList() {
        Long id = answer1.getAnswerID().getQuestionID();
        given(answerRepository.findAll(any(AnswerSpecification.class))).willReturn(List.of());
        List<AnswerSenderDTO> AnswerSenderDTOS = new ArrayList<>();
        assertEquals(AnswerSenderDTOS, underTesting.getAnswersByQuestion(id));
    }

    @Test
    void getAnswersByParticipant() {
        Long id = answer1.getAnswerID().getParticipantID();
        given(answerRepository.findAll(any(AnswerSpecification.class))).willReturn(List.of(answer1));
        List<AnswerSenderDTO> AnswerSenderDTOS = new ArrayList<>();
        AnswerSenderDTOS.add(mapper.map(answer1));
        assertEquals(AnswerSenderDTOS, underTesting.getAnswersByParticipant(id));
    }

    @Test
    void getAnswersByParticipantEmptyList() {
        Long id = answer1.getAnswerID().getParticipantID();
        given(answerRepository.findAll(any(AnswerSpecification.class))).willReturn(List.of());
        List<AnswerSenderDTO> AnswerSenderDTOS = new ArrayList<>();
        assertEquals(AnswerSenderDTOS, underTesting.getAnswersByParticipant(id));
    }

    @Test
    void getAnswersBySurvey() {
        Long surveyId = 5L;
        given(answerRepository.findAllBySurvey(surveyId)).willReturn(List.of(answer1, answer2));
        List<AnswerSenderDTO> AnswerSenderDTOS = new ArrayList<>();
        AnswerSenderDTOS.add(mapper.map(answer1));
        AnswerSenderDTOS.add(mapper.map(answer2));
        assertEquals(AnswerSenderDTOS, underTesting.getAnswersBySurvey(surveyId));
    }

    @Test
    void getAnswersBySurveyEmptyList() {
        Long surveyId = 5L;
        given(answerRepository.findAllBySurvey(surveyId)).willReturn(List.of());
        List<AnswerSenderDTO> AnswerSenderDTOS = new ArrayList<>();
        assertEquals(AnswerSenderDTOS, underTesting.getAnswersBySurvey(surveyId));
    }
}