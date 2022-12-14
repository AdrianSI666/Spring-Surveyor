package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.SurveyReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveySenderDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveyStatusDTO;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.exceptions.taken.passcode.PasscodeTakenException;
import com.griddynamics.internship.helloworld.mapper.SurveyMapperImpl;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.specification.SearchCriteria;
import com.griddynamics.internship.helloworld.specification.SurveySpecification;
import com.griddynamics.internship.helloworld.utils.SurveyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalTime;
import java.util.List;

import static com.griddynamics.internship.helloworld.service.UserService.USER_NOT_FOUND_MSG;

/**
 * Service layer for all business actions regarding survey entity.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    /**
     * Mapper for mapping survey entity to it's dto representation.
     */
    private final SurveyMapperImpl mapper;
    private final Clock clock;
    private final SurveyUtils surveyUtils;
    protected static final String SURVEY_NOT_FOUND_MSG = "Couldn't find survey with id: %d";
    protected static final String PASSCODE_TAKEN_MSG = "Survey with passcode: \"%s\" is already taken." +
            "Suggested passcode: %s";


    /**
     * Inserts a new survey into the database
     *
     * @param surveyReceiverDTO - dto object with fields necessary to create a survey in the database.
     * @return dto representation of the inserted survey.
     */
    public SurveySenderDTO addSurvey(SurveyReceiverDTO surveyReceiverDTO) {
        log.info("Adding survey: {}", surveyReceiverDTO);
        log.info(surveyReceiverDTO.authorId());
        User author = userRepository.findById(surveyReceiverDTO.authorId())
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG,
                        surveyReceiverDTO.authorId())));

        String passcode = surveyReceiverDTO.passcode();
        if (passcode == null || passcode.isEmpty()) {
            passcode = surveyUtils.generatePasscode();
        } else {
            surveyRepository.findByPasscode(passcode)
                    .ifPresent(survey -> {
                        final String suggestedPasscode = surveyUtils.generatePasscode();
                        throw new PasscodeTakenException(String.format(PASSCODE_TAKEN_MSG,
                                survey.getPasscode(), suggestedPasscode));
                    });
        }

        Survey survey = Survey.builder()
                .name(surveyReceiverDTO.name())
                .description(surveyReceiverDTO.description())
                .passcode(passcode)
                .duration(LocalTime.of(surveyReceiverDTO.hours(),
                        surveyReceiverDTO.minutes(),
                        surveyReceiverDTO.seconds()))
                .questions(List.of())
                .author(author)
                .participants(List.of())
                .started(false)
                .build();

        return mapper.map(surveyRepository.save(survey));
    }

    /**
     * Finds a survey in the database using its id.
     *
     * @param surveyID - id of the survey.
     * @return dto representation of the survey
     */
    public SurveySenderDTO getSurvey(Long surveyID) {
        log.info("Getting survey with id: {}", surveyID);

        return mapper.map(surveyRepository.findById(surveyID)
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyID))));
    }

    /**
     * Finds all the surveys that exist in the database.
     *
     * @return list with the survey dto representations.
     */
    public Page<SurveySenderDTO> getSurveys(Integer page) {
        log.info("Getting all surveys");
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Survey> surveyPage = surveyRepository.findAll(paging);
        return surveyPage.map(mapper::map);
    }

    /**
     * Finds all the surveys made by the particular user. Page number which ReactJS provides starts from 1,
     * but the page number that java provides starts from 0, that's why I needed to change page from parameter.
     *
     * @param authorID - the id of the user.
     * @return List with dto representations of the surveys,
     */
    public Page<SurveySenderDTO> getSurveysByAuthorID(String authorID, Integer page) {
        log.info("Getting survey by author id: {}", authorID);

        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize);
//        Page<Survey> surveyPage = surveyRepository.findAllByAuthorId(author.getId(), paging);
//        return surveyPage.map(mapper::map);
        SurveySpecification surveyByAuthorId = new SurveySpecification(new SearchCriteria("author_id", ":", authorID));
        return surveyRepository.findAll(surveyByAuthorId, paging)
                .map(mapper::map);
    }

    /**
     * Updates existing survey in the database.
     *
     * @param surveyID - id of the survey
     * @param survey   - dto with all the fields necessary to update survey in the database.
     * @return dto representation of the updated survey.
     */
    public SurveySenderDTO updateSurvey(Long surveyID, SurveyReceiverDTO survey) {
        log.info("Updating survey with id: {}", survey);

        Survey surveyToUpdate = surveyRepository.findById(surveyID)
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyID)));

        log.debug("Old survey: {}. New survey: {}", surveyToUpdate, survey);

        surveyToUpdate.setName(survey.name());
        surveyToUpdate.setDescription(survey.description());
        surveyToUpdate.setDuration(LocalTime.of(survey.hours(), survey.minutes(), survey.seconds()));

        return mapper.map(surveyRepository.save(surveyToUpdate));
    }

    /**
     * Deletes survey from the database
     *
     * @param surveyID - id of the survey
     */
    public void deleteSurvey(Long surveyID) {
        log.info("Deleting survey with id: {}", surveyID);

        surveyRepository.deleteById(surveyID);
    }

    /**
     * Starts the survey making it available for the questions to be accessed by the participants.
     *
     * @param surveyID - id of the survey.
     */
    public void startSurvey(Long surveyID) {
        log.info("Starting survey with id: {}", surveyID);
        Survey surveyToStart = surveyRepository.findById(surveyID)
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyID)));
        surveyToStart.setStarted(true);
        surveyToStart.setDateTimeStarted(clock.instant());
        surveyRepository.save(surveyToStart);
    }

    /**
     * Starts the survey closing the access to the questions for the participants.
     *
     * @param surveyID - id of the survey.
     */
    public void stopSurvey(Long surveyID) {
        log.info("Stopping survey with id: {}", surveyID);
        Survey surveyToStart = surveyRepository.findById(surveyID)
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyID)));

        surveyToStart.setStarted(false);
        surveyToStart.setPasscode(null);
        surveyRepository.save(surveyToStart);
    }

    /**
     * @param surveyID - id of survey which we want to check status.
     * @return survey status containing Boolean which shows if survey was started or not.
     */
    public SurveyStatusDTO getSurveyStatus(Long surveyID) {
        log.info("Getting status of survey with id: {}", surveyID);
        Survey surveyToCheckStatus = surveyRepository.findById(surveyID)
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyID)));
        return mapper.mapStatus(surveyToCheckStatus);
    }

    /**
     * Restart survey if it's status is not started or throw exception if it's currently running.
     *
     * @param surveyID - id of survey which we want to restart.
     */
    public void restartSurvey(Long surveyID) {
        log.info("Restarting survey with id: %d".formatted(surveyID));
        Survey surveyToRestart = surveyRepository.findById(surveyID)
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG, surveyID)));
        if (surveyToRestart.isStarted())
            throw new ForbiddenAccessException("Can't restart survey which is running.");
        surveyToRestart.setPasscode(surveyUtils.generatePasscode());
        surveyRepository.save(surveyToRestart);
    }
}
