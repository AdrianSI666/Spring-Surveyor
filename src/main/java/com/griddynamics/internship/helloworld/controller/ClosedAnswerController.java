package com.griddynamics.internship.helloworld.controller;

import com.griddynamics.internship.helloworld.dto.receiver.ClosedAnswerReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ClosedAnswerSenderDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.ClosedAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Path.CLOSED_ANSWER_VALUE + "s")
@RequiredArgsConstructor
public class ClosedAnswerController {
    private final ClosedAnswerService closedAnswerService;

    /**
     * @return http Response with the dto representations of all the closed answers in the database
     * with the 200 OK status code if there aren't any exceptions.
     */
    @GetMapping()
    public ResponseEntity<List<ClosedAnswerSenderDTO>> getClosedAnswers() {
        return ResponseEntity.ok().body(closedAnswerService.getClosedAnswers());
    }

    /**
     * Makes a call to the service layer to find a closed answer based on its id.
     *
     * @param id - id of the question.
     * @return http Response with the dto representation of the closed answer with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClosedAnswerSenderDTO> getClosedAnswersById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(closedAnswerService.getClosedAnswerById(id));
    }

    /**
     * Makes a call to the service layer to create a closed answer entity in the database.
     *
     * @param closedAnswerReceiverDTO - dto with all the customizable fields of the Closed Answer entity class.
     * @return http Response with the dto representation of closed answer with the 201 CREATED status code
     * if there aren't any exceptions.
     */
    @PostMapping()
    public ResponseEntity<ClosedAnswerSenderDTO> addClosedAnswer(@RequestBody ClosedAnswerReceiverDTO closedAnswerReceiverDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(closedAnswerService.addClosedAnswer(closedAnswerReceiverDTO));
    }

    /**
     * Makes a call to the service layer to update an existing closed answer in the database.
     *
     * @param closedAnswerReceiverDTO - dto with all the customizable fields of the answer entity class
     * @return http Response with the dto representation of closed answer with the 200 OK status code
     * if there aren't any exceptions.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClosedAnswerSenderDTO> updateClosedAnswer(@PathVariable Long id,
                                                              @RequestBody ClosedAnswerReceiverDTO closedAnswerReceiverDTO) {
        return ResponseEntity.ok().body(closedAnswerService.updateClosedAnswer(closedAnswerReceiverDTO, id));
    }

    /**
     * Makes a call to the service layer to delete a closed answer based on an embedded id consisting of
     * question and participant id.
     *
     * @param id - id of the ClosedAnswer.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteClosedAnswer(@PathVariable("id") Long id) {
        closedAnswerService.deleteClosedAnswer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to find all the closed answers connected to a question.
     *
     * @param id - id of the question.
     * @return http Response with the dto representations of the closed answers with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.QUESTION_VALUE + "/{questionId}")
    public ResponseEntity<List<ClosedAnswerSenderDTO>> getClosedAnswersByQuestion(@PathVariable("questionId") Long id) {
        return ResponseEntity.ok().body(closedAnswerService.getClosedAnswerByQuestionId(id));
    }
}
