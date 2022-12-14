package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.mother.QuestionMother;
import com.griddynamics.internship.helloworld.mother.SurveyMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuestionRepositoryTest extends AbstractContainerBaseTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void givenParticipant_whenSave_thenReturnSavedParticipant() {
        Survey survey = SurveyMother.twoMinuteNotStartedSurvey(1L).build();

        Question question = QuestionMother.basic()
                .survey(survey)
                .build();

        Question savedQuestion = questionRepository.save(question);

        Assertions.assertNotNull(savedQuestion);
        Assertions.assertNotNull(savedQuestion.getId());
        Assertions.assertEquals(question.getName(), question.getName());
        Assertions.assertEquals(question.getContent(), question.getContent());
    }
}
