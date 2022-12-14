package com.griddynamics.internship.helloworld.mother;

import com.griddynamics.internship.helloworld.domain.Question;

import java.sql.Time;
import java.util.ArrayList;

public class QuestionMother {
    public static Question.QuestionBuilder basic() {
        return Question.builder()
                .content("What is 2+2 ?")
                .answers(new ArrayList<>())
                .name("basic question");
    }
    public static Question.QuestionBuilder basic(Long id) {
        return Question.builder()
                .id(id)
                .content("What is 2+2 ?")
                .answers(new ArrayList<>())
                .name("basic question");
    }
}
