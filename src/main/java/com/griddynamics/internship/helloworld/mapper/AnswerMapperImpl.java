package com.griddynamics.internship.helloworld.mapper;

import com.griddynamics.internship.helloworld.domain.Answer;
import com.griddynamics.internship.helloworld.dto.sender.AnswerSenderDTO;
import org.springframework.stereotype.Component;

/**
 * This class is used to map answer entity from the database into a dto with fields necessary for the GET request.
 */
@Component
public class AnswerMapperImpl implements Mapper<Answer, AnswerSenderDTO> {
    @Override
    public AnswerSenderDTO map(Answer source) {
        String content = source.getContent();
        Long questionId = source.getQuestion().getId();
        String questionName = source.getQuestion().getName();
        String questionContent = source.getQuestion().getContent();
        Long participantId = source.getParticipant().getId();
        String participantNick = source.getParticipant().getNick();
        Long closedAnswerId = null;
        String closedAnswerContent = null;
        if(source.getChosen_answer() != null) {
            closedAnswerId = source.getChosen_answer().getId();
            closedAnswerContent = source.getChosen_answer().getContent();
        }
        return new AnswerSenderDTO(
                content,
                questionId,
                questionName,
                questionContent,
                participantId,
                participantNick,
                closedAnswerId,
                closedAnswerContent);
    }
}
