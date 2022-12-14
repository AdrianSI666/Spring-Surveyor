package com.griddynamics.internship.helloworld.mapper;

import com.griddynamics.internship.helloworld.domain.ClosedAnswer;
import com.griddynamics.internship.helloworld.dto.sender.ClosedAnswerSenderDTO;
import org.springframework.stereotype.Component;

/**
 * This class is used to map closed answer entity from the database into a dto with fields necessary for the GET request.
 */
@Component
public class ClosedAnswerMapperImpl implements Mapper<ClosedAnswer, ClosedAnswerSenderDTO> {
    @Override
    public ClosedAnswerSenderDTO map(ClosedAnswer source) {
        Long id = source.getId();
        String content = source.getContent();
        Integer value = source.getValue();
        Long questionId = source.getQuestion().getId();
        return new ClosedAnswerSenderDTO(id, content, value, questionId);
    }
}
