package com.griddynamics.internship.helloworld.controller;

import com.griddynamics.internship.helloworld.dto.receiver.SurveyReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveySenderDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveyStatusDTO;
import com.griddynamics.internship.helloworld.enums.Path;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
 * This class is a REST controller for survey resource. It does not return or receive entity objects. Instead, it
 * uses appropriate receiver and sender dto objects which present only those fields which are converted to JSON.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(Path.SURVEY_VALUE + "s")
public class SurveyController {

    private final SurveyService surveyService;

    /**
     * Makes a call to the service layer to create a survey entity in the database.
     *
     * @param survey - dto with all the customizable fields of the survey entity class.
     * @return http Response with the dto representation of survey with the 201 CREATED status code
     * if there aren't any exceptions.
     */
    @PostMapping()
    public ResponseEntity<SurveySenderDTO> addSurvey(@RequestBody SurveyReceiverDTO survey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(surveyService.addSurvey(survey));
    }

    /**
     * Makes a call to service layer to get surveys in relationship with user that created them. Page number which ReactJS
     * provides starts from 1, that's why default need to be 1 and also service layer as Java language starts counting from
     * 0, that's why we need to add one to it so ReactJS will behave properly.
     *
     * @param id   - id of author of surveys
     * @param page - current page in use
     * @return ResponseEntity with status code 200 and Map with Page details containing: surveys array of SurveySenderDTO,
     * currentPage number and totalPages number if there aren't any exceptions.
     */
    @GetMapping(Path.USER_VALUE + "/{id}")
    public ResponseEntity<Map<String, Object>> getSurveysByAuthorID(@PathVariable String id,
                                                                    @RequestParam(defaultValue = "1") Integer page) {
        Page<SurveySenderDTO> surveySenderDTOPage = surveyService.getSurveysByAuthorID(id, page);
        List<SurveySenderDTO> surveys = surveySenderDTOPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("surveys", surveys);
        response.put("currentPage", surveySenderDTOPage.getNumber() + 1);
        response.put("totalPages", surveySenderDTOPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    /**
     * Makes a call to get specific survey by its id.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representation of the survey with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SurveySenderDTO> getSurvey(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok().body(surveyService.getSurvey(id));
    }

    /**
     * Makes a call to the service layer to find all the surveys in the database.
     *
     * @return http Response with the dto representations of the surveys with the 200 OK status code
     * if there aren't any exceptions.
     */
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getSurveys(@RequestParam(defaultValue = "1") Integer page) {
        Page<SurveySenderDTO> surveySenderDTOPage = surveyService.getSurveys(page);
        List<SurveySenderDTO> surveys = surveySenderDTOPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("surveys", surveys);
        response.put("currentPage", surveySenderDTOPage.getNumber() + 1);
        response.put("totalPages", surveySenderDTOPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    /**
     * Makes a call to the service layer to update an existing survey in the database.
     *
     * @param survey - dto with all the customizable fields of the survey entity class
     * @return http Response with the dto representation of survey with the 200 OK status code
     * if there aren't any exceptions.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SurveySenderDTO> updateSurvey(@PathVariable Long id, @RequestBody SurveyReceiverDTO survey)
            throws NotFoundException {
        return ResponseEntity.ok().body(surveyService.updateSurvey(id, survey));
    }

    /**
     * Makes a call to the service layer to delete a survey based on his id.
     *
     * @param id - id of the survey.
     * @return http Response with the dto representation of the deleted survey with 200 OK status code
     * if there aren't any exceptions.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to start the survey
     *
     * @param id - id of the survey.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<HttpStatus> startSurvey(@PathVariable Long id) {
        surveyService.startSurvey(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to stop the survey
     *
     * @param id - id of the survey.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @PatchMapping("/{id}/stop")
    public ResponseEntity<HttpStatus> stopSurvey(@PathVariable Long id) {
        surveyService.stopSurvey(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Makes a call to the service layer to find out if the survey has started.
     *
     * @param id - id of the survey.
     * @return http Response with a boolean flag and the 200 OK status code if there aren't any exceptions.
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<SurveyStatusDTO> isStartedSurvey(@PathVariable Long id) {
        return ResponseEntity.ok().body(surveyService.getSurveyStatus(id));
    }

    /**
     * A call to restart a Survey so iit can be started again.
     *
     * @param id - id of the survey we want to restart.
     * @return http Response with the 200 OK status code if there aren't any exceptions.
     */
    @PatchMapping("/{id}/restart")
    public ResponseEntity<HttpStatus> restartSurvey(@PathVariable Long id) {
        surveyService.restartSurvey(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
