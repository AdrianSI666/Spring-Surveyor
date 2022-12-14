package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.mother.ParticipantMother;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ParticipantRepositoryTest extends AbstractContainerBaseTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    void givenParticipant_whenSave_thenReturnSavedParticipant() {
        User user = UserMother.basic("1L")
                .createdSurveys(new ArrayList<>())
                .build();

        Survey survey = SurveyMother.twoMinuteNotStartedSurvey(1L)
                .author(user)
                .build();

        Participant participant = ParticipantMother.basic()
                .survey(survey)
                .build();

        Participant savedParticipant = participantRepository.save(participant);

        Assertions.assertNotNull(savedParticipant);
        Assertions.assertNotNull(savedParticipant.getId());
        Assertions.assertEquals(participant.getNick(), savedParticipant.getNick());
    }
}
