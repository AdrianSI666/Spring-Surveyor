package com.griddynamics.internship.helloworld.integration;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.sender.QuestionSenderDTO;
import com.griddynamics.internship.helloworld.mapper.QuestionMapperImpl;
import com.griddynamics.internship.helloworld.mother.QuestionMother;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.service.QuestionService;
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
class QuestionServiceIntegrationTest extends AbstractContainerBaseTest {
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionMapperImpl mapper;

    @Test
    void givenSurveyId_whenGetQuestionsBySurvey_thenReturnQuestionListOfThatSurvey() {
        User user = UserMother.basic()
                .build();

        Survey importantSurvey = SurveyMother.twoMinuteNotStartedSurvey(1L)
                .author(user)
                .build();

        Survey notImportantSurvey = SurveyMother.twoMinuteNotStartedSurvey(2L)
                .author(user)
                .build();

        Question question1 = QuestionMother.basic(1L)
                .name("q1")
                .survey(importantSurvey)
                .build();

        Question question2 = QuestionMother.basic(2L)
                .name("q2")
                .survey(importantSurvey)
                .build();

        Question question3 = QuestionMother.basic(3L)
                .name("q3")
                .survey(notImportantSurvey)
                .build();

        importantSurvey.getQuestions().add(question1);
        importantSurvey.getQuestions().add(question2);
        notImportantSurvey.getQuestions().add(question3);

        userRepository.save(user);
        surveyRepository.save(importantSurvey);
        surveyRepository.save(notImportantSurvey);
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        List<QuestionSenderDTO> surveyQuestions = questionService.getQuestionsBySurvey(importantSurvey.getId());
        Assertions.assertEquals(2, surveyQuestions.size());
        Assertions.assertTrue(surveyQuestions.containsAll(Stream.of(question1, question2).map(mapper::map).toList()));
    }
}
