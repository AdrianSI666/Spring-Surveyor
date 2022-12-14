package com.griddynamics.internship.helloworld.integration;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.sender.UserSenderDTO;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.service.UserService;
import com.griddynamics.internship.helloworld.utils.SurveyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"com.griddynamics.internship.helloworld.service", "com.griddynamics.internship.helloworld.utils"})
class UserServiceIntegrationTest extends AbstractContainerBaseTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    SurveyRepository surveyRepository;

    @Test
    void givenSurveyId_whenGetUserBySurveyId_thenReturnAuthorOfSurvey() {
        User user = UserMother.basic("1L")
                .build();

        Survey survey = SurveyMother.twoMinuteNotStartedSurvey(1L)
                .author(user)
                .build();

        user.getCreatedSurveys().add(survey);

        userRepository.save(user);
        surveyRepository.save(survey);

        UserSenderDTO savedUser = userService.getUserBySurveyId(survey.getId());

        Assertions.assertNotNull(savedUser);
        Assertions.assertNotNull(savedUser.id());
        Assertions.assertEquals(savedUser.name(), user.getName());
        Assertions.assertEquals(savedUser.surname(), user.getSurname());
    }
}
