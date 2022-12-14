package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.ClosedAnswer;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.dto.receiver.ClosedAnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ClosedAnswerSenderDTO;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.mapper.ClosedAnswerMapperImpl;
import com.griddynamics.internship.helloworld.repository.ClosedAnswerRepository;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.specyfication.ClosedAnswerSpecification;
import com.griddynamics.internship.helloworld.specyfication.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.griddynamics.internship.helloworld.service.QuestionService.QUESTION_NOT_FOUND_MSG;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClosedAnswerService {
    private final ClosedAnswerRepository closedAnswerRepository;
    private final QuestionRepository questionRepository;
    /**
     * Mapper for mapping ClosedAnswer entity to it's dto representation.
     */
    private final ClosedAnswerMapperImpl mapper;

    protected static final String CLOSED_ANSWER_NOT_FOUND_MSG = "Couldn't find closed answer with id: %d";

    /**
     * Finds all the ClosedAnswers that exist in the database.
     *
     * @return list with the ClosedAnswer dto representations.
     */
    public List<ClosedAnswerSenderDTO> getClosedAnswers() {
        log.info("Getting all ClosedAnswers");
        return closedAnswerRepository.findAll(Sort.by("id"))
                .stream()
                .map(mapper::map)
                .toList();
    }

    /**
     * Inserts a new ClosedAnswer into the database
     *
     * @param closedAnswerReceiverDTO - dto object with fields necessary to create an ClosedAnswer in the database.
     * @return dto representation of the inserted ClosedAnswer.
     */
    public ClosedAnswerSenderDTO addClosedAnswer(ClosedAnswerReceiverDTO closedAnswerReceiverDTO) {
        log.info("Adding content to database: {}", closedAnswerReceiverDTO.content());
        Long questionId = closedAnswerReceiverDTO.questionId();
        String content = closedAnswerReceiverDTO.content();
        Integer value = closedAnswerReceiverDTO.value();
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new NotFoundException(String.format(QUESTION_NOT_FOUND_MSG, questionId)));
        return mapper.map(closedAnswerRepository.save(ClosedAnswer.builder()
                .content(content)
                .value(value)
                .question(question)
                .build()));
    }

    /**
     * Updates existing ClosedAnswer in the database.
     *
     * @param closedAnswerReceiverDTO - dto with all the fields necessary to update in the database.
     * @param closedAnswerId          - id of closed answer to update
     * @return dto representation of the updated ClosedAnswer.
     */
    public ClosedAnswerSenderDTO updateClosedAnswer(ClosedAnswerReceiverDTO closedAnswerReceiverDTO, Long closedAnswerId) {
        log.info("Updating closed answer with id: {}", closedAnswerId);
        ClosedAnswer closedAnswer = closedAnswerRepository.findById(closedAnswerId).orElseThrow(
                () -> new NotFoundException(String.format(CLOSED_ANSWER_NOT_FOUND_MSG, closedAnswerId)));
        closedAnswer.setContent(closedAnswerReceiverDTO.content());
        closedAnswer.setValue(closedAnswerReceiverDTO.value());
        return mapper.map(closedAnswerRepository.save(closedAnswer));
    }

    /**
     * Deletes ClosedAnswer from the database.
     *
     * @param closedAnswerId - id of closed answer to delete
     */
    public void deleteClosedAnswer(Long closedAnswerId) {
        log.info("Deleting closed answer with id: {}", closedAnswerId);
        ClosedAnswer closedAnswer = closedAnswerRepository.findById(closedAnswerId).orElseThrow(
                () -> new NotFoundException(String.format(CLOSED_ANSWER_NOT_FOUND_MSG, closedAnswerId)));
        closedAnswerRepository.delete(closedAnswer);
    }

    /**
     * Finds a particular ClosedAnswer in database.
     *
     * @param closedAnswerId - id of the closedAnswer that needs to be received.
     * @return dto representation of the ClosedAnswer.
     */
    public ClosedAnswerSenderDTO getClosedAnswerById(Long closedAnswerId) {
        log.info("Getting ClosedAnswer by id: {}", closedAnswerId);
        return mapper.map(closedAnswerRepository.findById(closedAnswerId).orElseThrow(
                () -> new NotFoundException(String.format(CLOSED_ANSWER_NOT_FOUND_MSG, closedAnswerId))));
    }

    /**
     * Finds a ClosedAnswer in relationship with id.
     *
     * @param questionId - id of the question that the ClosedAnswer belongs to.
     * @return dto representation of the ClosedAnswer.
     */
    public List<ClosedAnswerSenderDTO> getClosedAnswerByQuestionId(Long questionId) {
        log.info("Getting ClosedAnswers by question id: {}", questionId);
        ClosedAnswerSpecification closedAnswerByQuestionId = new ClosedAnswerSpecification(
                new SearchCriteria("questionId", ":", questionId));
        return closedAnswerRepository.findAll(closedAnswerByQuestionId)
                .stream()
                .map(mapper::map)
                .toList();
    }
}
