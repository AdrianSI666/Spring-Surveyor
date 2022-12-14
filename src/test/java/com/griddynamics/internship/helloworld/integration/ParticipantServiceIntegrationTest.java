package com.griddynamics.internship.helloworld.integration;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.sender.ParticipantSenderDTO;
import com.griddynamics.internship.helloworld.mapper.ParticipantMapperImpl;
import com.griddynamics.internship.helloworld.mother.ParticipantMother;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import com.griddynamics.internship.helloworld.repository.ParticipantRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.service.ParticipantService;
import com.griddynamics.internship.helloworld.utils.SurveyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"com.griddynamics.internship.helloworld.service", "com.griddynamics.internship.helloworld.utils"})
class ParticipantServiceIntegrationTest extends AbstractContainerBaseTest {
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ParticipantService participantService;
    @Autowired
    ParticipantMapperImpl mapper;

    @Test
    void givenSurveyId_whenGetParticipantsBySurvey_thenReturnParticipantListOfSurvey() {
        User user = UserMother.basic()
                .build();

        Survey importantSurvey = SurveyMother.twoMinuteNotStartedSurvey(1L)
                .author(user)
                .build();

        Survey notImportantSurvey = SurveyMother.twoMinuteNotStartedSurvey(2L)
                .author(user)
                .build();

        Participant participant1 = ParticipantMother.basic(1L)
                .nick("p1")
                .survey(importantSurvey)
                .build();

        Participant participant2 = ParticipantMother.basic(2L)
                .nick("p2")
                .survey(importantSurvey)
                .build();

        Participant participant3 = ParticipantMother.basic(3L)
                .nick("p3")
                .survey(notImportantSurvey)
                .build();

        importantSurvey.getParticipants().add(participant1);
        importantSurvey.getParticipants().add(participant2);
        notImportantSurvey.getParticipants().add(participant3);

        userRepository.save(user);
        surveyRepository.save(importantSurvey);
        surveyRepository.save(notImportantSurvey);
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        participantRepository.save(participant3);

        List<ParticipantSenderDTO> surveyParticipants = participantService.getParticipantsBySurvey(importantSurvey.getId());
        Assertions.assertEquals(2, surveyParticipants.size());
        Assertions.assertTrue(surveyParticipants.containsAll(Stream.of(participant1, participant2)
                .map(mapper::map)
                .toList()));
    }
}
