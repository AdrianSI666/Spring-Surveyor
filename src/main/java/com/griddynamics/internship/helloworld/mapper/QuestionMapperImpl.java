package com.griddynamics.internship.helloworld.mapper;

import com.griddynamics.internship.helloworld.domain.Question;
import com.griddynamics.internship.helloworld.dto.sender.QuestionSenderDTO;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * This class is used to map question entity from the database into a dto with fields necessary for the GET request.
 */
@Component
public class QuestionMapperImpl implements Mapper<Question, QuestionSenderDTO> {

    @Override
    public QuestionSenderDTO map(Question source) {
        Long id = source.getId();
        String name = source.getName();
        String content = source.getContent();
        LocalTime duration = source.getSurvey().getDuration();
        Long surveyId = source.getSurvey().getId();
        return new QuestionSenderDTO(id, name, content, duration.getHour(), duration.getMinute(), duration.getSecond(), surveyId);
    }
}
