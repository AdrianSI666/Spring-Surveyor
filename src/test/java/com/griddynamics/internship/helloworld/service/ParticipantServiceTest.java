package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.ParticipantReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ParticipantSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.conflict.DataConflictException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.ParticipantMapperImpl;
import com.griddynamics.internship.helloworld.repository.ParticipantRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.specification.ParticipantSpecification;
import com.griddynamics.internship.helloworld.specification.SurveySpecification;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.griddynamics.internship.helloworld.service.ParticipantService.NICKNAME_TAKEN;
import static com.griddynamics.internship.helloworld.service.ParticipantService.PARTICIPANT_NOT_FOUND_MSG;
import static com.griddynamics.internship.helloworld.service.ParticipantService.WRONG_PASSCODE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private SurveyRepository surveyRepository;
    @Spy
    private ParticipantMapperImpl mapper;

    @InjectMocks
    private ParticipantService underTesting;
    private final Survey survey = Survey.builder()
            .id(5L)
            .name("SurveyTest")
            .description("survDesc")
            .passcode("pass")
            .duration(LocalTime.of(0, 5, 0))
            .questions(new ArrayList<>())
            .participants(new ArrayList<>())
            .author(User.builder().name("userName").surname("userSur").build())
            .started(false)
            .build();

    private final Participant participant1 = Participant.builder()
            .id(5L)
            .nick("mockTestNick")
            .survey(survey)
            .answers(null)
            .build();

    private final Participant participant2 = Participant.builder()
            .id(6L)
            .nick("mockTesstNickSecond")
            .survey(survey)
            .answers(null)
            .build();

    @Test
    void getParticipants() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize, Sort.by("id"));
        Page<Participant> participantPage = new PageImpl<>(List.of(participant1, participant2), paging, 0);
        given(participantRepository.findAll(paging)).willReturn(participantPage);

        Page<ParticipantSenderDTO> questionSenderDTOSFromService = underTesting.getParticipants(1);
        Page<ParticipantSenderDTO> questionSenderDTOS = participantPage.map(mapper::map);

        assertEquals(questionSenderDTOS, questionSenderDTOSFromService);
    }

    @Test
    void getParticipantsEmptyList() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize, Sort.by("id"));
        Page<Participant> participantPage = new PageImpl<>(List.of(), paging, 0);
        given(participantRepository.findAll(paging)).willReturn(participantPage);

        Page<ParticipantSenderDTO> questionSenderDTOSFromService = underTesting.getParticipants(1);
        Page<ParticipantSenderDTO> questionSenderDTOS = participantPage.map(mapper::map);

        assertEquals(questionSenderDTOS, questionSenderDTOSFromService);
    }

    @Test
    void getParticipantById() {
        given(participantRepository.findById(5L)).willReturn(Optional.of(participant1));
        assertEquals(mapper.map(participant1), underTesting.getParticipantById(5L));
    }

    @Test
    void getParticipantByIdWillThrowNotFoundWhenOptionalIsEmpty() {
        Long id = participant1.getId();
        given(participantRepository.findById(id)).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.getParticipantById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(PARTICIPANT_NOT_FOUND_MSG, id));
    }

    @Test
    void addParticipant() {
        String passcode = "passcode";
        ParticipantReceiverDTO participantReceiverDTO = new ParticipantReceiverDTO(participant1.getNick(),
                passcode);
        given(surveyRepository.findOne(any(SurveySpecification.class))).willReturn(Optional.of(survey));
        given(participantRepository.save(any())).willReturn(participant1);
        underTesting.addParticipant(participantReceiverDTO);

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();

        assertEquals(capturedParticipant.getNick(), participant1.getNick());
        assertEquals(capturedParticipant.getSurvey(), participant1.getSurvey());
        assertEquals(capturedParticipant.getAnswers(), participant1.getAnswers());
    }

    @Test
    void addParticipantThrowsSurveyNotFoundException() {
        String passcode = "passcode";
        ParticipantReceiverDTO participantReceiverDTO = new ParticipantReceiverDTO(participant1.getNick(),
                passcode);
        given(surveyRepository.findOne(any(SurveySpecification.class))).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTesting.addParticipant(participantReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(WRONG_PASSCODE, passcode));
    }

    @Test
    void addParticipantThrowsDataConflictException() {
        survey.getParticipants().add(participant1);
        survey.getParticipants().add(participant2);
        String passcode = "passcode";
        ParticipantReceiverDTO participantReceiverDTO = new ParticipantReceiverDTO(participant1.getNick(),
                passcode);
        given(surveyRepository.findOne(any(SurveySpecification.class))).willReturn(Optional.of(survey));

        assertThatThrownBy(() -> underTesting.addParticipant(participantReceiverDTO))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining(String.format(NICKNAME_TAKEN, participantReceiverDTO.nick()));
    }

    @Test
    void updateParticipant() {
        Long id = participant1.getId();
        String passcode = "pass2";
        given(participantRepository.findById(id)).willReturn(Optional.of(participant1));
        ParticipantReceiverDTO participantReceiverDTO = new ParticipantReceiverDTO(participant2.getNick(),
                passcode);
        given(participantRepository.save(any())).willReturn(participant2);
        underTesting.updateParticipant(id, participantReceiverDTO);

        ArgumentCaptor<Participant> argumentCaptor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(argumentCaptor.capture());
        Participant capturedParticipant = argumentCaptor.getValue();

        assertEquals(capturedParticipant.getNick(), participant2.getNick());
    }

    @Test
    void updateParticipantThrowsNotFoundParticipant() {
        Long id = participant1.getId();
        String passcode = "pass2";
        given(participantRepository.findById(id)).willReturn(Optional.empty());
        ParticipantReceiverDTO participantReceiverDTO = new ParticipantReceiverDTO(participant2.getNick(),
                passcode);
        assertThatThrownBy(() -> underTesting.updateParticipant(id, participantReceiverDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(PARTICIPANT_NOT_FOUND_MSG, id));
    }

    @Test
    void deleteParticipant() {
        given(participantRepository.findById(anyLong())).willReturn(Optional.of(participant1));
        underTesting.deleteParticipant(participant1.getId());
        then(participantRepository).should().delete(participant1);
    }

    @Test
    void deleteParticipantThrowParticipantNotFound() {
        Long id = participant1.getId();
        given(participantRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThatThrownBy(() -> underTesting.deleteParticipant(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format(PARTICIPANT_NOT_FOUND_MSG, participant1.getId()));
    }

    @Test
    void getParticipantsBySurvey() {
        Long id = participant1.getSurvey().getId();
        given(participantRepository.findAll(any(ParticipantSpecification.class))).willReturn(List.of(participant1, participant2));
        List<ParticipantSenderDTO> participantSenderDTOS = new ArrayList<>();
        participantSenderDTOS.add(mapper.map(participant1));
        participantSenderDTOS.add(mapper.map(participant2));
        assertEquals(participantSenderDTOS, underTesting.getParticipantsBySurvey(id));
    }

    @Test
    void getParticipantsBySurveyReturnsEmptyList() {
        Long id = participant1.getSurvey().getId();
        given(participantRepository.findAll(any(ParticipantSpecification.class))).willReturn(List.of());
        List<ParticipantSenderDTO> participantSenderDTOS = new ArrayList<>();
        assertEquals(participantSenderDTOS, underTesting.getParticipantsBySurvey(id));
    }
}