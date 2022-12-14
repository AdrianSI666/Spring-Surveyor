package com.griddynamics.internship.helloworld.integration;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.sender.SurveySenderDTO;
import com.griddynamics.internship.helloworld.mapper.SurveyMapperImpl;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.service.SurveyService;
import com.griddynamics.internship.helloworld.utils.SurveyUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"com.griddynamics.internship.helloworld.service", "com.griddynamics.internship.helloworld.utils"})
class SurveyServiceIntegrationTest extends AbstractContainerBaseTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    SurveyMapperImpl mapper;

    @Test
    void givenAuthorID_whenGetSurveysByAuthorID_thenReturnSurveyListOfThatAuthor() {
        User author = UserMother.basic("1L")
                .name("author")
                .build();

        User notAuthor = UserMother.basic("2L")
                .name("not author")
                .build();

        Survey survey1 = SurveyMother.twoMinuteNotStartedSurvey(1L)
                .author(author)
                .build();

        Survey survey2 = SurveyMother.twoMinuteNotStartedSurvey(2L)
                .author(author)
                .build();

        Survey survey3 = SurveyMother.twoMinuteNotStartedSurvey(3L)
                .author(notAuthor)
                .build();

        author.getCreatedSurveys().add(survey1);
        author.getCreatedSurveys().add(survey2);

        userRepository.save(author);
        userRepository.save(notAuthor);
        surveyRepository.save(survey1);
        surveyRepository.save(survey2);
        surveyRepository.save(survey3);


        Page<SurveySenderDTO> authorSurveys = surveyService.getSurveysByAuthorID(author.getId(), 1);
        Assertions.assertEquals(2, authorSurveys.getTotalElements());
        Assertions.assertTrue(authorSurveys.getContent()
                .containsAll(Stream.of(survey1, survey2).map(mapper::map).toList()));
    }
}
