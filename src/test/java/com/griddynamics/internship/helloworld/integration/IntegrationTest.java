package com.griddynamics.internship.helloworld.integration;

import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.AnswerID;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;
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
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Test class for integration test of the survey creation process.
 * By działały dodaj run -> edit configurations w env variable do testu poszczególnych profili:
 * TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock;
 * DOCKER_HOST=unix:///Users/waszaNazwaUseraNaMacu/.colima/docker.sock
 * #Gdy używa się Colimy, przy Docker Desktop nie trzeba
 */
@Testcontainers
@DataJpaTest
public class IntegrationTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:latest"))
            .withDatabaseName("quizapp")
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432);

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> String.format("jdbc:postgresql://localhost:%d/quizapp", container.getFirstMappedPort()));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }

    @Before
    public void init() {
        User user = UserMother.basic()
                .build();

        Survey survey = SurveyMother.twoMinuteNotStartedSurvey(1L)
                .author(user)
                .build();

        Question question1 = QuestionMother.basic(1L)
                .survey(survey)
                .build();

        Question question2 = QuestionMother.basic(2L)
                .survey(survey)
                .build();

        Participant participant = ParticipantMother.basic(1L)
                .survey(survey)
                .build();

        Answer answer1 = AnswerMother.basic()
                .question(question1)
                .participant(participant)
                .answerID(new AnswerID(question1.getId(), participant.getId()))
                .build();

        Answer answer2 = AnswerMother.basic()
                .question(question1)
                .participant(participant)
                .answerID(new AnswerID(question2.getId(), participant.getId()))
                .build();


        userRepository.save(user);
        surveyRepository.save(survey);
        questionRepository.save(question1);
        questionRepository.save(question2);
        participantRepository.save(participant);
        answerRepository.save(answer1);
        answerRepository.save(answer2);
    }
}