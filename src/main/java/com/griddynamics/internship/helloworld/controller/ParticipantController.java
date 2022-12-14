package com.griddynamics.internship.helloworld.controller;

import com.griddynamics.internship.helloworld.dto.receiver.ParticipantReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.ParticipantSenderDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.service.ParticipantService;
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
 * This class is a REST controller for participant resource. It does not return or receive entity objects. Instead, it
 * uses appropriate receiver and sender dto objects which present only those fields which are converted to JSON.
 */
@RestController
@RequestMapping(Path.PARTICIPANT_VALUE + "s")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    /**
     * Makes a call to the service layer to find all the participants in the database.
     *
     * @return http Response with the dto representations of the participants with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllParticipants(@RequestParam(defaultValue = "1") Integer page) {
        Page<ParticipantSenderDTO> participantSenderDTO = participantService.getParticipants(page);
        List<ParticipantSenderDTO> participants = participantSenderDTO.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("participants", participants);
        response.put("currentPage", participantSenderDTO.getNumber() + 1);
        response.put("totalPages", participantSenderDTO.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    /**
     * Makes a call to receive information of specific participant.
     *
     * @param id - id of the participant.
     * @return http Response with the dto representation of the participant with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParticipantSenderDTO> getParticipant(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(participantService.getParticipantById(id));
    }

    /**
     * Makes a call to the service layer to create a participant entity in the database.
     *
     * @param participantReceiverDTO - dto with all the customizable fields of the participant entity class.
     * @return http Response with the dto representation of participant with the 201 CREATED status code
     * if there aren't any exceptions.
     */
    @PostMapping()
    public ResponseEntity<ParticipantSenderDTO> addParticipant(@RequestBody ParticipantReceiverDTO participantReceiverDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(participantService.addParticipant(participantReceiverDTO));
    }

    /**
     * Makes a call to the service layer to update an existing participant in the database.
     *
     * @param participantReceiverDTO - dto with all the customizable fields of the participant entity class
     * @return http Response with the dto representation of participant with the 200 OK status code
     * if there aren't any exceptions.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ParticipantSenderDTO> updateParticipant(@PathVariable("id") Long id,
                                                                  @RequestBody ParticipantReceiverDTO participantReceiverDTO) {
        return ResponseEntity.ok().body(participantService.updateParticipant(id, participantReceiverDTO));
    }

    /**
     * Makes a call to the service layer to delete a participant based on his id.
     *
     * @param id - id of the participant.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteParticipant(@PathVariable("id") Long id) {
        participantService.deleteParticipant(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to find all the participants taking part in a survey.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representations of the participants with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping(Path.SURVEY_VALUE + "/{id}")
    public ResponseEntity<List<ParticipantSenderDTO>> getParticipantsBySurveyId(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(participantService.getParticipantsBySurvey(id));
    }
}
