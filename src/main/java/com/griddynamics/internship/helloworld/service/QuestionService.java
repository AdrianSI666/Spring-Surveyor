package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.dto.receiver.QuestionReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.QuestionSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.forbidden.ForbiddenAccessException;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.QuestionMapperImpl;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.specification.QuestionSpecification;
import com.griddynamics.internship.helloworld.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static com.griddynamics.internship.helloworld.service.SurveyService.SURVEY_NOT_FOUND_MSG;

/**
 * Service layer for all business actions regarding question entity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SurveyRepository surveyRepository;
    /**
     * Mapper for mapping question entity to it's dto representation.
     */
    private final QuestionMapperImpl mapper;
    private final Clock clock;
    protected static final String QUESTION_NOT_FOUND_MSG = "Couldn't find question with id: %d";

    /**
     * Finds all the questions that exist in the database.
     *
     * @return list with the question dto representations.
     */
    public Page<QuestionSenderDTO> getQuestions(Integer page) {
        log.info("Getting all questions");
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("id"));
        Page<Question> questionPage = questionRepository.findAll(paging);
        return questionPage.map(mapper::map);
    }

    /**
     * Finds a question in the database using its id.
     *
     * @param questionId - id of the question.
     * @return dto representation of the question
     */
    public QuestionSenderDTO getQuestion(Long questionId) {
        log.info("Getting question by id: " + questionId);
        return mapper.map(questionRepository.findById(questionId).orElseThrow(
                () -> new NotFoundException(String.format(QUESTION_NOT_FOUND_MSG, questionId))));
    }

    /**
     * Inserts a new question into the database
     *
     * @param questionReceiverDTO - dto object with fields necessary to create a participant in the database.
     * @return dto representation of the inserted question.
     */
    public QuestionSenderDTO addQuestion(QuestionReceiverDTO questionReceiverDTO) {
        log.info("Adding question with name: " + questionReceiverDTO.name());
        Survey survey = surveyRepository.findById(questionReceiverDTO.surveyId())
                .orElseThrow(() -> new NotFoundException(String.format(SURVEY_NOT_FOUND_MSG,
                        questionReceiverDTO.surveyId())));

        Question question = Question.builder()
                .name(questionReceiverDTO.name())
                .content(questionReceiverDTO.content())
                .survey(survey)
                .build();

        return mapper.map(questionRepository.save(question));
    }

    /**
     * Updates existing question in the database.
     *
     * @param questionId          - id of the question
     * @param questionReceiverDTO - dto with all the fields necessary to update a question in the database.
     * @return dto representation of the updated question.
     */
    public QuestionSenderDTO updateQuestion(Long questionId, QuestionReceiverDTO questionReceiverDTO) {
        log.info("Updating question with id: " + questionId);
        Question questionToUpdate = questionRepository.findById(questionId).orElseThrow(
                () -> new NotFoundException(String.format(QUESTION_NOT_FOUND_MSG, questionId)));
        questionToUpdate.setName(questionReceiverDTO.name());
        questionToUpdate.setContent(questionReceiverDTO.content());
        return mapper.map(questionRepository.save(questionToUpdate));
    }

    /**
     * Deletes question from the database
     *
     * @param questionId - id of the question
     */
    public void deleteQuestion(Long questionId) {
        log.info("Deleting question with id: " + questionId);
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new NotFoundException(String.format(QUESTION_NOT_FOUND_MSG, questionId)));
        questionRepository.delete(question);
    }

    /**
     * Finds all the questions belonging to the survey.
     *
     * @param surveyId - id of the survey.
     * @return list with dto representations of the questions.
     */
    public List<QuestionSenderDTO> getQuestionsBySurvey(Long surveyId) {
        log.info("Getting questions by survey id: " + surveyId);
        QuestionSpecification questionBySurveyId = new QuestionSpecification(new SearchCriteria("survey_id",
                ":",
                surveyId));
        return questionRepository.findAll(questionBySurveyId)
                .stream()
                .map(mapper::map)
                .toList();
    }

    /**
     * Finds the question that should be presented to the survey participant
     * based on the time that has elapsed from the start of the survey.
     *
     * @param surveyId - id of the survey.
     * @return dto representation of the question.
     */
    public QuestionSenderDTO getNextQuestionInSurvey(Long surveyId) {
        log.info("Getting next question in survey: %d".formatted(surveyId));
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new NotFoundException(SURVEY_NOT_FOUND_MSG.formatted(surveyId)));
        if (!survey.isStarted())
            throw new ForbiddenAccessException("Can't get questions, because survey did not start yet");
        QuestionSpecification questionBySurveyId = new QuestionSpecification(new SearchCriteria("surveyId",
                ":",
                surveyId));
        List<Question> questions = questionRepository.findAll(questionBySurveyId);

        LocalTime questionDuration = survey.getDuration();
        Instant timeStarted = survey.getDateTimeStarted();
        Instant instant = clock.instant();

        long timePassed = instant.getEpochSecond() - timeStarted.getEpochSecond();
        int durationOfSurvey = questionDuration.toSecondOfDay() * questions.size();
        if (timePassed >= durationOfSurvey)
            throw new ForbiddenAccessException("Can't get any more questions, survey was already ended.");
        long questionNumber = Long.divideUnsigned(timePassed, questionDuration.toSecondOfDay());

        return mapper.map(questions.get((int) questionNumber));
    }
}
