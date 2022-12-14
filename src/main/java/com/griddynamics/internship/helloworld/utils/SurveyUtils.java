package com.griddynamics.internship.helloworld.utils;

import com.griddynamics.internship.helloworld.repository.SurveyRepository;
import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Class created for the purpose of delivering utilities methods for class {@link com.griddynamics.internship.helloworld.domain.Survey}
 */
@Component
@AllArgsConstructor
public class SurveyUtils {

    /**
     * Passcode length needed for the {@link SurveyUtils#generatePasscode()}
     */
    private static final int PASSCODE_LENGTH = 8;

    private final SurveyRepository surveyRepository;

    /**
     * Method which generates unique passcode which consists of numbers and letters.
     * Length of the passcode is declared according to the {@link SurveyUtils#PASSCODE_LENGTH PASSCODE_LENGTH variable}.
     * This method uses {@link SurveyRepository#existsSurveyByPasscode(String)} to check if generated passcode is in database.
     *
     * @return passcode String consisting letters and numbers
     */
    public String generatePasscode() {
        boolean hasLetters = true;
        boolean hasNumbers = true;
        String generatedPasscode;
        do {
            generatedPasscode = RandomStringUtils.random(PASSCODE_LENGTH, hasLetters, hasNumbers)
                    .toUpperCase(Locale.ROOT);
        } while (surveyRepository.existsSurveyByPasscode(generatedPasscode));
        return generatedPasscode;
    }
}
