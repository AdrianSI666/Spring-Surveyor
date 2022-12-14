package com.griddynamics.internship.helloworld.dto.receiver;

public record ClosedAnswerReceiverDTO(
        String content,
        Integer value,
        Long questionId
) {

}
