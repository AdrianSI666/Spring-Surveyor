package com.griddynamics.internship.helloworld.mapper;

import com.griddynamics.internship.helloworld.domain.Survey;
import com.griddynamics.internship.helloworld.dto.sender.SurveySenderDTO;
import com.griddynamics.internship.helloworld.dto.sender.SurveyStatusDTO;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * This class is used to map survey entity from the database into a dto with fields necessary for the GET request.
 */
@Component
public class SurveyMapperImpl implements Mapper<Survey, SurveySenderDTO> {
    @Override
    public SurveySenderDTO map(Survey source) {
        Long id = source.getId();
        String name = source.getName();
        String description = source.getDescription();
        String passcode = source.getPasscode();
        LocalTime duration = source.getDuration();
        Integer second = duration.getSecond();
        Integer minutes = duration.getMinute();
        Integer hours = duration.getHour();
        String authorId = source.getAuthor().getId();
        String authorName = source.getAuthor().getName();
        String authorSurname = source.getAuthor().getSurname();
        Boolean isSurveyStarted = source.isStarted();
        return new SurveySenderDTO(id, name, description, passcode, hours, minutes, second, authorId, authorName, authorSurname, isSurveyStarted);
    }

    /**
     * Method that maps only status from Survey to DTO. It's made to have fewer methods usage which would be unneeded
     * in method that is repeated every sec for every user.
     *
     * @param source Survey from which the status will be obtained.
     * @return DTO with one Boolean field which represents if survey is started or not.
     */
    public SurveyStatusDTO mapStatus(Survey source) {
        Boolean isSurveyStarted = source.isStarted();
        return new SurveyStatusDTO(isSurveyStarted);
    }
}
