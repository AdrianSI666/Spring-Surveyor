package com.griddynamics.internship.helloworld.mother;

import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.domain.Participant;

import java.util.ArrayList;

public class ParticipantMother {
    public static Participant.ParticipantBuilder basic() {
        return Participant.builder()
                .nick("Basic")
                .answers(new ArrayList<>());
    }
    public static Participant.ParticipantBuilder basic(Long id) {
        return Participant.builder()
                .id(id)
                .nick("Basic")
                .answers(new ArrayList<>());
    }
}
