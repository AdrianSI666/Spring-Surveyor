package com.griddynamics.internship.helloworld.mother;

import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.AnswerID;
import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.domain.Question;

public class AnswerMother {
    public static Answer.AnswerBuilder basic() {
        return Answer.builder()
                .content("basic answer");
    }

    public static Answer.AnswerBuilder basic(Participant participant, Question question) {
        return Answer.builder()
                .participant(participant)
                .question(question)
                .answerID(new AnswerID(participant.getId(), question.getId()))
                .content("basic answer");
    }
}