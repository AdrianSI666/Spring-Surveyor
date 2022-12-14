package com.griddynamics.internship.helloworld.controller;

import com.griddynamics.internship.helloworld.dto.receiver.QuestionReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.QuestionSenderDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.QuestionService;
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
 * This class is a REST controller for question resource. It does not return or receive entity objects. Instead, it
 * uses appropriate receiver and sender dto objects which present only those fields which are converted to JSON.
 */
@RestController
@RequestMapping(Path.QUESTION_VALUE + "s")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Makes a call to the service layer to find all the questions in the database.
     *
     * @return http Response with the dto representations of the questions with the 200 OK status code if there aren't any exceptions.
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getQuestions(@RequestParam(defaultValue = "1") Integer page) {
        Page<QuestionSenderDTO> questionSenderDTOPage = questionService.getQuestions(page);
        List<QuestionSenderDTO> questions = questionSenderDTOPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        response.put("currentPage", questionSenderDTOPage.getNumber() + 1);
        response.put("totalPages", questionSenderDTOPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    /**
     * Makes a call to receive specific question with provided id.
     *
     * @param id - id of the question.
     * @return http Response with the dto representation of the question with 200 the OK status code if there aren't any exceptions.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionSenderDTO> getQuestion(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(questionService.getQuestion(id));
    }

    /**
     * Makes a call to the service layer to create a question entity in the database.
     *
     * @param questionReceiverDTO - dto with all the customizable fields of the question entity class.
     * @return http Response with the dto representation of question with the 201 CREATED status code
     * if there aren't any exceptions.
     */
    @PostMapping("")
    public ResponseEntity<QuestionSenderDTO> addQuestion(@RequestBody QuestionReceiverDTO questionReceiverDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.addQuestion(questionReceiverDTO));
    }

    /**
     * Makes a call to the service layer to update an existing question in the database.
     *
     * @param questionReceiverDTO - dto with all the customizable fields of the question entity class
     * @return http Response with the dto representation of question with the 200 OK status code
     * if there aren't any exceptions.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionSenderDTO> updateQuestion(@PathVariable("id") Long id,
                                                            @RequestBody QuestionReceiverDTO questionReceiverDTO) {
        return ResponseEntity.ok().body(questionService.updateQuestion(id, questionReceiverDTO));
    }

    /**
     * Makes a call to the service layer to delete a question based on his id.
     *
     * @param id - id of the question.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteQuestion(@PathVariable("id") Long id) {
        questionService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to find all the questions belonging to a survey.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representations of the questions with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.SURVEY_VALUE + "/{id}")
    public ResponseEntity<List<QuestionSenderDTO>> getQuestionsBySurvey(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(questionService.getQuestionsBySurvey(id));
    }

    /**
     * Makes a call to the service layer to get the current question in the survey.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representations of the questions with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.SURVEY_VALUE + "/{id}/next")
    public ResponseEntity<QuestionSenderDTO> getNextQuestionInSurvey(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionService.getNextQuestionInSurvey(id));
    }
}
