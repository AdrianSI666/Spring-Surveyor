package com.griddynamics.internship.helloworld.controller;

import com.griddynamics.internship.helloworld.dto.receiver.AnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.AnswerSenderDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a REST controller for answer resource. It does not return or receive entity objects. Instead, it
 * uses appropriate receiver and sender dto objects which present only those fields which are converted to JSON.
 */
@RestController
@RequestMapping(Path.ANSWER_VALUE + "s")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    /**
     * @return http Response with the dto representations of all the answers in the database
     * with the 200 OK status code if there aren't any exceptions.
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAnswers(@RequestParam(defaultValue = "1") Integer page) {
        Page<AnswerSenderDTO> answerSenderDTO = answerService.getAnswers(page);
        List<AnswerSenderDTO> answers = answerSenderDTO.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("answers", answers);
        response.put("currentPage", answerSenderDTO.getNumber() + 1);
        response.put("totalPages", answerSenderDTO.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    /**
     * Makes a call to the service layer to create an answer entity in the database.
     *
     * @param answerReceiverDTO - dto with all the customizable fields of the answer entity class.
     * @return http Response with the dto representation of answer with the 201 CREATED status code
     * if there aren't any exceptions.
     */
    @PostMapping()
    public ResponseEntity<AnswerSenderDTO> addAnswer(@RequestBody AnswerReceiverDTO answerReceiverDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(answerService.addAnswer(answerReceiverDTO));
    }

    /**
     * Makes a call to the service layer to update an existing answer in the database.
     *
     * @param answerReceiverDTO - dto with all the customizable fields of the answer entity class
     * @return http Response with the dto representation of answer with the 200 OK status code
     * if there aren't any exceptions.
     */
    @PutMapping()
    public ResponseEntity<AnswerSenderDTO> updateAnswer(@RequestBody AnswerReceiverDTO answerReceiverDTO) {
        return ResponseEntity.ok().body(answerService.updateAnswer(answerReceiverDTO));
    }

    /**
     * Makes a call to the service layer to delete an answer based on an embedded id consisting of
     * question and participant id.
     *
     * @param questionID    - id of the question.
     * @param participantID - id of the participant.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @DeleteMapping(Path.QUESTION_VALUE + "/{questionID}/participant/{participantId}")
    public ResponseEntity<HttpStatus> deleteAnswer(@PathVariable("questionID") Long questionID,
                                                   @PathVariable("participantId") Long participantID) {
        answerService.deleteAnswer(questionID, participantID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to find all the answers connected to a question.
     *
     * @param id - id of the question.
     * @return http Response with the dto representations of the answers with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.QUESTION_VALUE + "/{questionID}")
    public ResponseEntity<List<AnswerSenderDTO>> getAnswersByQuestion(@PathVariable("questionID") Long id) {
        return ResponseEntity.ok().body(answerService.getAnswersByQuestion(id));
    }

    /**
     * Makes a call to the service layer to find all the answers made by a participant.
     *
     * @param id - id of the participant.
     * @return http Response with the dto representations of the answers with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.PARTICIPANT_VALUE + "/{participantId}")
    public ResponseEntity<List<AnswerSenderDTO>> getAnswersByParticipant(@PathVariable("participantId") Long id) {
        return ResponseEntity.ok().body(answerService.getAnswersByParticipant(id));
    }

    /**
     * Makes a call to the service layer to find all the answers belonging to a whole survey.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representations of the answers with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.SURVEY_VALUE + "/{surveyId}")
    public ResponseEntity<List<AnswerSenderDTO>> getAnswersBySurvey(@PathVariable("surveyId") Long id) {
        return ResponseEntity.ok().body(answerService.getAnswersBySurvey(id));
    }

    /**
     * Makes a call to the service layer to find an answer based on an embedded id consisting of
     * question and participant id.
     *
     * @param questionId    - id of the question.
     * @param participantId - id of the participant.
     * @return http Response with the dto representation of the answer with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.QUESTION_VALUE + "/{questionID}" + Path.PARTICIPANT_VALUE + "/{participantId}")
    public ResponseEntity<AnswerSenderDTO> getAnswersByQuestionAndByParticipant(
            @PathVariable("questionID") Long questionId,
            @PathVariable("participantId") Long participantId) {
        return ResponseEntity.ok().body(answerService.getAnswerByQuestionIdAndParticipantId(questionId, participantId));
    }
}
