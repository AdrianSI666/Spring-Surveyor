package com.griddynamics.internship.helloworld.integration;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
import com.griddynamics.internship.helloworld.dto.sender.AnswerSenderDTO;
import com.griddynamics.internship.helloworld.mapper.AnswerMapperImpl;
import com.griddynamics.internship.helloworld.mother.AnswerMother;
import com.griddynamics.internship.helloworld.mother.ParticipantMother;
import com.griddynamics.internship.helloworld.mother.QuestionMother;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import com.griddynamics.internship.helloworld.mother.UserMother;
import com.griddynamics.internship.helloworld.repository.AnswerRepository;
import com.griddynamics.internship.helloworld.repository.ParticipantRepository;
import com.griddynamics.internship.helloworld.repository.QuestionRepository;
import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import com.griddynamics.internship.helloworld.repository.UserRepository;
import com.griddynamics.internship.helloworld.service.AnswerService;
import com.griddynamics.internship.helloworld.utils.SurveyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
class AnswerServiceIntegrationTest extends AbstractContainerBaseTest {
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    AnswerService answerService;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AnswerMapperImpl mapper;

    @Disabled("Specification issue")
    @Test
    void givenQuestionId_whenGetAnswersByQuestion_thenReturnAnswerListOfThatQuestion() {
        User author = UserMother.basic()
                .build();

        Survey survey = SurveyMother.twoMinuteNotStartedSurvey()
                .author(author)
                .build();

        Question importantQuestion = QuestionMother.basic(1L)
                .survey(survey)
                .name("important")
                .build();

        Question notImportantQuestion = QuestionMother.basic(2L)
                .survey(survey)
                .name("not important")
                .build();

        Participant participant1 = ParticipantMother.basic(1L)
                .survey(survey)
                .build();

        Participant participant2 = ParticipantMother.basic(2L)
                .survey(survey)
                .build();

        Answer answer1 = AnswerMother.basic(participant1, importantQuestion)
                .content("a1")
                .build();

        Answer answer2 = AnswerMother.basic(participant2, importantQuestion)
                .content("a2")
                .build();

        Answer answer3 = AnswerMother.basic(participant1, notImportantQuestion)
                .content("a3")
                .build();

        Answer answer4 = AnswerMother.basic(participant2, notImportantQuestion)
                .content("a4")
                .build();

        userRepository.save(author);
        surveyRepository.save(survey);
        questionRepository.save(importantQuestion);
        questionRepository.save(notImportantQuestion);
        participantRepository.save(participant1);
        participantRepository.save(participant2);
        answerRepository.save(answer1);
        answerRepository.save(answer2);
        answerRepository.save(answer3);
        answerRepository.save(answer4);

        List<AnswerSenderDTO> questionAnswers = answerService.getAnswersByQuestion(importantQuestion.getId());
        Assertions.assertEquals(2, questionAnswers.size());
        Assertions.assertTrue(questionAnswers.containsAll(Stream.of(answer1, answer2).map(mapper::map).toList()));
    }
}
