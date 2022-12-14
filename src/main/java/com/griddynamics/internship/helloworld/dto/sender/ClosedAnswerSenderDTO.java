package com.griddynamics.internship.helloworld.dto.sender;

public record ClosedAnswerSenderDTO (
        Long id,
        String content,
        Integer value,
        Long questionId
){
}
