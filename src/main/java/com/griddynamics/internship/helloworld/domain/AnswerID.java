package com.griddynamics.internship.helloworld.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Embedded answer ID consisting of question id and participant id.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class AnswerID implements Serializable {
    @Column(name = "question_id")
    private Long questionID;

    @Column(name = "participant_id")
    private Long participantID;

}
