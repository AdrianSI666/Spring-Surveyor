package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.AnswerID;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.dto.receiver.AnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.AnswerSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.AnswerMapperImpl;
import com.griddynamics.internship.helloworld.repository.AnswerRepository;
import com.griddynamics.internship.helloworld.repository.ParticipantRepository;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.specification.AnswerSpecification;
import com.griddynamics.internship.helloworld.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.griddynamics.internship.helloworld.service.ParticipantService.PARTICIPANT_NOT_FOUND_MSG;
import static com.griddynamics.internship.helloworld.service.QuestionService.QUESTION_NOT_FOUND_MSG;

/**
 * Service layer for all business actions regarding answer entity.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@ComponentScan(basePackages = {"com.griddynamics.internship.helloworld.mapper"})
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final ParticipantRepository participantRepository;
    private final QuestionRepository questionRepository;
    /**
     * Mapper for mapping answer entity to it's dto representation.
     */
    private final AnswerMapperImpl mapper;
    private static final String QUESTIONID = "questionId";
    private static final String PARTICIPANTID = "participantId";
    private static final String EQUAL = ":";

    protected static final String ANSWER_NOT_FOUND_MSG = "Couldn't find answer given by %d for question %d";

    /**
     * Finds all the answers that exist in the database.
     *
     * @return list with the answer dto representations.
     */
    public Page<AnswerSenderDTO> getAnswers(Integer page) {
        log.info("Getting all answers from database");
        int pageSize = 5;
        page -= 1;
        Pageable paging = PageRequest.of(page, pageSize, Sort.by("questionId"));
        Page<Answer> answerPage = answerRepository.findAll(paging);
        return answerPage.map(mapper::map);
    }

    /**
     * Inserts a new answer into the database
     *
     * @param answerReceiverDTO - dto object with fields necessary to create an answer in the database.
     * @return dto representation of the inserted answer.
     */
    public AnswerSenderDTO addAnswer(AnswerReceiverDTO answerReceiverDTO) {
        log.info("Adding content to database");
        Long questionId = answerReceiverDTO.questionId();
        Long participantId = answerReceiverDTO.participantId();
        String answer = answerReceiverDTO.answer();
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new NotFoundException(String.format(QUESTION_NOT_FOUND_MSG, questionId)));
        Participant participant = participantRepository.findById(participantId).orElseThrow(
                () -> new NotFoundException(String.format(PARTICIPANT_NOT_FOUND_MSG, participantId)));
        return mapper.map(answerRepository.save(Answer.builder()
                .answerID(AnswerID.builder().questionID(questionId).participantID(participantId).build())
                .content(answer)
                .question(question)
                .participant(participant)
                .build()));
    }

    /**
     * Updates existing answer in the database.
     *
     * @param answerReceiverDTO - dto with all the fields necessary to update in the database.
     * @return dto representation of the updated answer.
     */
    public AnswerSenderDTO updateAnswer(AnswerReceiverDTO answerReceiverDTO) {
        log.info("Updating content in database");
        AnswerSpecification answerByQuestionId = new AnswerSpecification(
                new SearchCriteria(QUESTIONID, EQUAL, answerReceiverDTO.questionId()));
        AnswerSpecification answerByParticipantId = new AnswerSpecification(
                new SearchCriteria(PARTICIPANTID, EQUAL, answerReceiverDTO.participantId()));
        Answer answer = answerRepository.findOne(Specification.where(answerByQuestionId).and(answerByParticipantId))
                .orElseThrow(
                        () -> new NotFoundException(String.format(ANSWER_NOT_FOUND_MSG,
                                answerReceiverDTO.participantId(),
                                answerReceiverDTO.questionId())));
        answer.setContent(answerReceiverDTO.answer());
        return mapper.map(answerRepository.save(answer));
    }

    /**
     * Deletes answer from the database.
     *
     * @param questionId    - id of the question that the answer belongs to.
     * @param participantId - id of the participant that submitted the answer.
     */
    public void deleteAnswer(Long questionId, Long participantId) {
        log.info("Deleting content from database");
        AnswerSpecification answerByQuestionId = new AnswerSpecification(
                new SearchCriteria(QUESTIONID, EQUAL, questionId));
        AnswerSpecification answerByParticipantId = new AnswerSpecification(
                new SearchCriteria(PARTICIPANTID, EQUAL, participantId));
        Answer answer = answerRepository.findOne(Specification.where(answerByQuestionId).and(answerByParticipantId)).orElseThrow(
                () -> new NotFoundException(String.format(ANSWER_NOT_FOUND_MSG,
                        participantId,
                        questionId)));
        answerRepository.delete(answer);
    }

    /**
     * Finds a particular answer in the database.
     *
     * @param questionId    - id of the question that the answer belongs to.
     * @param participantId - id of the participant that submitted the answer.
     * @return dto representation of the answer.
     */
    public AnswerSenderDTO getAnswerByQuestionIdAndParticipantId(Long questionId, Long participantId) {
        log.info("Getting answers by question and participant from database");
        AnswerSpecification answerByQuestionId = new AnswerSpecification(
                new SearchCriteria(QUESTIONID, EQUAL, questionId));
        AnswerSpecification answerByParticipantId = new AnswerSpecification(
                new SearchCriteria(PARTICIPANTID, EQUAL, participantId));
        return mapper.map(answerRepository.findOne(Specification.where(answerByQuestionId).and(answerByParticipantId))
                .orElseThrow(
                        () -> new NotFoundException(String
                                .format(ANSWER_NOT_FOUND_MSG,
                                        questionId,
                                        participantId))));
    }

    /**
     * Finds all the answers to the particular question.
     *
     * @param questionId - id of the question.
     * @return List with dto representations of the answers.
     */
    public List<AnswerSenderDTO> getAnswersByQuestion(Long questionId) {
        log.info("Getting answers by question from database");
        AnswerSpecification answerByQuestion = new AnswerSpecification(
                new SearchCriteria(QUESTIONID, EQUAL, questionId));
        return answerRepository.findAll(answerByQuestion)
                .stream()
                .map(mapper::map)
                .toList();
    }

    /**
     * Finds all the answers submitted by the participant.
     *
     * @param participantId - id of the participant.
     * @return List with dto representation of the answers.
     */
    public List<AnswerSenderDTO> getAnswersByParticipant(Long participantId) {
        log.info("Getting answers by participant from database");
        AnswerSpecification answerByParticipantId = new AnswerSpecification(
                new SearchCriteria(PARTICIPANTID, EQUAL, participantId));
        return answerRepository.findAll(answerByParticipantId)
                .stream()
                .map(mapper::map)
                .toList();
    }

    /**
     * Finds all the answers submitted in a survey.
     *
     * @param surveyId - id of the survey.
     * @return List with dto representation of the answers.
     */
    public List<AnswerSenderDTO> getAnswersBySurvey(Long surveyId) {
        log.info("Getting answers by survey from database");
        return answerRepository.findAllBySurvey(surveyId)
                .stream()
                .map(mapper::map)
                .toList();
    }
}
