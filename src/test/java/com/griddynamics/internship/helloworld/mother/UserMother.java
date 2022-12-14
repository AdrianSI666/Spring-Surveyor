package com.griddynamics.internship.helloworld.mother;

import com.griddynamics.internship.helloworld.domain.User;

import java.util.ArrayList;

public class UserMother {
    public static User.UserBuilder basic() {
        return User.builder()
                .name("Jan")
                .surname("Kowalski")
                .createdSurveys(new ArrayList<>());
    }

    public static User.UserBuilder basic(String id) {
        return User.builder()
                .id(id)
                .name("Jan")
                .surname("Kowalski")
                .createdSurveys(new ArrayList<>());
    }
}
