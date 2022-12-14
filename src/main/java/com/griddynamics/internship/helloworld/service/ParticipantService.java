package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.dto.receiver.ParticipantReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ParticipantSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.conflict.DataConflictException;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.ParticipantMapperImpl;
import com.griddynamics.internship.helloworld.repository.ParticipantRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.specification.ParticipantSpecification;
import com.griddynamics.internship.helloworld.specification.SearchCriteria;
import com.griddynamics.internship.helloworld.specification.SurveySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service layer for all business actions regarding participant entity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final SurveyRepository surveyRepository;
    /**
     * Mapper for mapping participant entity to it's dto representation.
     */
    private final ParticipantMapperImpl mapper;
    protected static final String PARTICIPANT_NOT_FOUND_MSG = "Couldn't find participant with id: %d";
    protected static final String WRONG_PASSCODE = "Couldn't find survey with passcode: %s";
    protected static final String NICKNAME_TAKEN = "Nickname: %s is already taken";

    /**
     * Finds all the participants that exist in the database.
     *
     * @return list with the participant dto representations.
     */
    public Page<ParticipantSenderDTO> getParticipants(Integer page) {
        log.info("Getting all participants");
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("id"));
        Page<Participant> participantPage = participantRepository.findAll(paging);
        return participantPage.map(mapper::map);
    }

    /**
     * Finds participant by his id.
     *
     * @param participantID - id of the participant.
     * @return dto representation of the participant.
     */
    public ParticipantSenderDTO getParticipantById(Long participantID) {
        log.info("Getting participant by id: " + participantID);
        return mapper.map(participantRepository.findById(participantID)
                .orElseThrow(() -> new NotFoundException(String.format(PARTICIPANT_NOT_FOUND_MSG, participantID))));
    }

    /**
     * Adds a new participant to the survey and inserts him into the database.
     *
     * @param participantReceiverDTO - dto object with fields necessary to create a participant in the database.
     * @return dto representation of the inserted participant.
     */
    public ParticipantSenderDTO addParticipant(ParticipantReceiverDTO participantReceiverDTO) {
        log.info("Adding participant with name: " + participantReceiverDTO.nick());
        SurveySpecification surveyByPasscode = new SurveySpecification(new SearchCriteria("passcode",
                ":",
                participantReceiverDTO.passcode()));
        Survey survey = surveyRepository.findOne(surveyByPasscode)
                .orElseThrow(() -> new NotFoundException(String.format(WRONG_PASSCODE,
                        participantReceiverDTO.passcode())));
        if (survey.isStarted()) throw new ForbiddenAccessException("Can't join survey that already started!");

        boolean nickTaken = survey.getParticipants().stream()
                .anyMatch(p -> Objects.equals(p.getNick(), participantReceiverDTO.nick()));

        if (nickTaken) {
                throw new DataConflictException(String.format(NICKNAME_TAKEN, participantReceiverDTO.nick()));
        }
        log.info("New participant %s successfully passed every check".formatted(participantReceiverDTO.nick()));
        Participant participant = Participant.builder()
                .nick(participantReceiverDTO.nick())
                .survey(survey)
                .build();
        return mapper.map(participantRepository.save(participant));
    }

    /**
     * Updates existing participant in the database.
     *
     * @param participantId          - id of the participant
     * @param participantReceiverDTO - dto with all the fields necessary to update the participant in the database.
     * @return dto representation of the updated participant.
     */
    public ParticipantSenderDTO updateParticipant(Long participantId, ParticipantReceiverDTO participantReceiverDTO) {
        log.info("Updating participant with id: " + participantId);
        Participant participantToUpdate = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(String.format(PARTICIPANT_NOT_FOUND_MSG, participantId)));
        participantToUpdate.setNick(participantReceiverDTO.nick());
        return mapper.map(participantRepository.save(participantToUpdate));
    }

    /**
     * Deletes participant from the database
     *
     * @param participantId - id of the participant.
     */
    public void deleteParticipant(Long participantId) {
        log.info("Deleting participant with id: " + participantId);
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(String.format(PARTICIPANT_NOT_FOUND_MSG, participantId)));
        participantRepository.delete(participant);
    }

    /**
     * Finds all the participants taking part in the survey.
     *
     * @param surveyId - id of the survey.
     * @return list with dto representations of the participants.
     */
    public List<ParticipantSenderDTO> getParticipantsBySurvey(Long surveyId) {
        log.info("Getting participants by survey it: " + surveyId);
        ParticipantSpecification participantBySurveyId = new ParticipantSpecification(new SearchCriteria("survey_id",
                ":",
                surveyId));
        return participantRepository.findAll(participantBySurveyId)
                .stream()
                .map(mapper::map)
                .toList();
    }
}
