package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SurveyRepositoryTest extends AbstractContainerBaseTest {
    @Autowired
    SurveyRepository surveyRepository;

    @Test
    void givenAnswers_whenSave_thenReturnSavedAnswer() {
        User user = UserMother.basic("1L")
                .build();

        Survey survey = SurveyMother.twoMinuteNotStartedSurvey()
                .author(user)
                .build();

        Survey savedSurvey = surveyRepository.save(survey);

        Assertions.assertNotNull(savedSurvey);
        Assertions.assertEquals(savedSurvey.getAuthor().getName(), user.getName());
        Assertions.assertEquals(savedSurvey.getAuthor().getSurname(), user.getSurname());
    }
}
