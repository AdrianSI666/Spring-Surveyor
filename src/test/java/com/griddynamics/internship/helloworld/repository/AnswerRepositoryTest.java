package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.AbstractContainerBaseTest;
import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.mother.AnswerMother;
import com.griddynamics.internship.helloworld.mother.ParticipantMother;
import com.griddynamics.internship.helloworld.mother.QuestionMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AnswerRepositoryTest extends AbstractContainerBaseTest {
    @Autowired
    private AnswerRepository answerRepository;

    @Test
    void givenAnswers_whenSave_thenReturnSavedAnswer() {
        Participant participant = ParticipantMother.basic(1L)
                .build();

        Question question = QuestionMother.basic(1L)
                .build();

        Answer answer = AnswerMother.basic(participant, question).build();

        Answer savedAnswer = answerRepository.save(answer);

        Assertions.assertNotNull(savedAnswer);
        Assertions.assertNotNull(savedAnswer.getAnswerID());
    }
}
