package com.griddynamics.internship.helloworld.service;

import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.receiver.SurveyReceiverDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveySenderDTO;
import com.griddynamics.internship.helloworld.exceptions.not.found.NotFoundException;
import com.griddynamics.internship.helloworld.exceptions.taken.passcode.PasscodeTakenException;
import com.griddynamics.internship.helloworld.mapper.SurveyMapperImpl;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.specification.SurveySpecification;
import com.griddynamics.internship.helloworld.utils.SurveyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {
    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private SurveyMapperImpl surveyMapper;

    @Mock
    private SurveyUtils surveyUtils;
    @Captor
    private ArgumentCaptor<Survey> surveyArgumentCaptor;

    @Mock
    private Clock clock;

    private static final ZonedDateTime NOW = ZonedDateTime.of(
            2022,
            11,
            7,
            12,
            30,
            30,
            0,
            ZoneId.of("UTC")
    );

    @InjectMocks
    private SurveyService testedSurveyService;

    private final User user = User.builder()
            .id("1L")
            .name("testName")
            .surname("testLast")
            .createdSurveys(null)
            .build();
    private final Survey survey = Survey.builder()
            .id(1L)
            .name("survName")
            .description("testDesc")
            .passcode("pass")
            .duration(LocalTime.of(0, 5, 0))
            .questions(null)
            .author(user)
            .participants(null)
            .started(false)
            .build();

    private final Survey secondSurvey = Survey.builder()
            .id(2L)
            .name(null)
            .description(null)
            .passcode(null)
            .duration(LocalTime.of(0, 5, 0))
            .questions(null)
            .author(user)
            .participants(null)
            .started(false)
            .build();

    @BeforeEach
    void setUp() {
        user.setCreatedSurveys(List.of(survey, secondSurvey));
    }

    @Test
    void addSurvey_method_test() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(surveyRepository.save(any())).willReturn(survey);

        SurveyReceiverDTO surveyReceiverDTO = new SurveyReceiverDTO(survey.getName(), survey.getDescription(),
                0, 5, 0, survey.getPasscode(), survey.getAuthor().getId());

        testedSurveyService.addSurvey(surveyReceiverDTO);

        verify(surveyRepository).save(surveyArgumentCaptor.capture());

        Survey surveyCaptured = surveyArgumentCaptor.getValue();
        assertEquals(surveyCaptured.getName(), survey.getName());
        assertEquals(surveyCaptured.getDescription(), survey.getDescription());
        assertEquals(surveyCaptured.getPasscode(), survey.getPasscode());
        assertEquals(surveyCaptured.getAuthor(), survey.getAuthor());
    }

    @Test
    void getSurvey_method_test() {
        given(surveyRepository.findById(survey.getId())).willReturn(Optional.of(survey));

        SurveySenderDTO surveyFromService = testedSurveyService.getSurvey(survey.getId());
        SurveySenderDTO surveySenderDTO = surveyMapper.map(survey);

        assertEquals(surveyFromService, surveySenderDTO);
    }

    @Test
    void getSurveys_method_test() {
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize);
        Page<Survey> surveyPage = new PageImpl<>(List.of(survey, secondSurvey), paging, 2);
        given(surveyRepository.findAll(paging)).willReturn(surveyPage);

        Page<SurveySenderDTO> surveySenderDTOSFromService = testedSurveyService.getSurveys(1);
        Page<SurveySenderDTO> surveySenderDTOS = surveyPage.map(surveyMapper::map);

        assertEquals(surveySenderDTOS, surveySenderDTOSFromService);
    }

    @Test
    void getSurveysByAuthorID_method_test() {
//        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        int pageSize = 5;
        Pageable paging = PageRequest.of(0, pageSize);
        Page<Survey> surveyPage = new PageImpl<>(List.of(survey, secondSurvey), paging, 2);
//        given(surveyRepository.findAllByAuthorId(user.getId(), paging)).willReturn(surveyPage);
        given(surveyRepository.findAll(any(SurveySpecification.class), any(Pageable.class))).willReturn(surveyPage);

        Page<SurveySenderDTO> surveySenderDTOSFromService = testedSurveyService.getSurveysByAuthorID(user.getId(), 1);
        Page<SurveySenderDTO> surveySenderDTOS = surveyPage.map(surveyMapper::map);

        assertEquals(surveySenderDTOS, surveySenderDTOSFromService);
    }

    @Test
    void updateSurvey_method_test() {
        SurveyReceiverDTO surveyReceiverDTO = new SurveyReceiverDTO("newName", "newDesc",
                0, 5, 0, "newPass", user.getId());

        given(surveyRepository.findById(survey.getId())).willReturn(Optional.of(survey));
        given(surveyRepository.save(any())).willReturn(survey);

        testedSurveyService.updateSurvey(survey.getId(), surveyReceiverDTO);

        verify(surveyRepository).save(surveyArgumentCaptor.capture());

        Survey captureSurvey = surveyArgumentCaptor.getValue();

        assertEquals(survey.getName(), captureSurvey.getName());
        assertEquals(survey.getDescription(), captureSurvey.getDescription());
        assertEquals(survey.getPasscode(), captureSurvey.getPasscode());
    }

    @Test
    void deleteSurvey_method_test() {
        testedSurveyService.deleteSurvey(1L);
        verify(surveyRepository, times(1)).deleteById(1L);
    }

    @Test
    void getSurvey_throws_exception() {
        given(surveyRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> testedSurveyService.getSurvey(2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateSurvey_throws_exception() {
        SurveyReceiverDTO surveyReceiverDTO = new SurveyReceiverDTO(null, null, 0, 5, 0, "newPass", "2L");
        Long id = survey.getId();
        given(surveyRepository.findById(survey.getId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> testedSurveyService.updateSurvey(id, surveyReceiverDTO))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void startSurvey() {
        Long id = survey.getId();
        given(surveyRepository.findById(id)).willReturn(Optional.of(survey));
        given(clock.instant()).willReturn(NOW.toInstant());

        testedSurveyService.startSurvey(id);
        survey.setStarted(true);
        survey.setDateTimeStarted(clock.instant());

        verify(surveyRepository, times(1)).save(surveyArgumentCaptor.capture());
        Survey captureSurvey = surveyArgumentCaptor.getValue();
        assertEquals(survey, captureSurvey);
    }

    @Test
    void stopSurvey() {
        Long id = survey.getId();
        given(surveyRepository.findById(id)).willReturn(Optional.of(survey));
        testedSurveyService.stopSurvey(id);

        survey.setStarted(false);
        verify(surveyRepository, times(1)).save(surveyArgumentCaptor.capture());

        Survey captureSurvey = surveyArgumentCaptor.getValue();
        assertEquals(survey, captureSurvey);
    }

    @Test
    void addSurvey_with_already_taken_passcode_throws_exception() {
        SurveyReceiverDTO surveyReceiverDTO = new SurveyReceiverDTO(null, null, 1, 1, 1,
                survey.getPasscode(), "2L");

        given(userRepository.findById(surveyReceiverDTO.authorId())).willReturn(Optional.ofNullable(user));
        given(surveyRepository.findByPasscode(survey.getPasscode())).willReturn(Optional.of(survey));

        assertThrows(PasscodeTakenException.class, () -> testedSurveyService.addSurvey(surveyReceiverDTO));
    }

    @Test
    void free_passcode_after_survey_finished() {
        given(surveyRepository.findById(survey.getId())).willReturn(Optional.of(survey));

        testedSurveyService.stopSurvey(survey.getId());

        assertNull(survey.getPasscode());
    }

}
