package com.griddynamics.internship.helloworld.mother;

import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.domain.User;

import java.time.LocalTime;
import java.util.ArrayList;

public class SurveyMother {
    public static Survey.SurveyBuilder twoMinuteNotStartedSurvey(Long id) {
        return Survey.builder()
                .passcode("default")
                .name("basic survey")
                .id(id)
                .questions(new ArrayList<>())
                .participants(new ArrayList<>())
                .description("basic description")
                .started(false)
                .duration(LocalTime.of(0, 2));
    }

    public static Survey.SurveyBuilder twoMinuteNotStartedSurvey() {
        return Survey.builder()
                .passcode("default")
                .name("basic survey")
                .description("basic description")
                .questions(new ArrayList<>())
                .participants(new ArrayList<>())
                .started(false)
                .duration(LocalTime.of(0, 2));
    }
}
