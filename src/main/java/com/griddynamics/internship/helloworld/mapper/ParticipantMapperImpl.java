package com.griddynamics.internship.helloworld.mapper;

import com.griddynamics.internship.helloworld.domain.Participant;
import com.griddynamics.internship.helloworld.dto.sender.ParticipantSenderDTO;
import org.springframework.stereotype.Component;

/**
 * This class is used to map participant entity from the database into a dto with fields necessary for the GET request.
 */
@Component
public class ParticipantMapperImpl implements Mapper<Participant, ParticipantSenderDTO> {

    @Override
    public ParticipantSenderDTO map(Participant source) {
        Long id = source.getId();
        String nick = source.getNick();
        Long surveyId = source.getSurvey().getId();
        return new ParticipantSenderDTO(id, nick, surveyId);
    }
}
