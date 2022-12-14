package com.griddynamics.internship.helloworld.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity for any answer to a question. Represents basic information about the answer.
 * Is in a relationship with {@link Question}, {@link Participant}.
 * Has an embedded Id (see {@link AnswerID}).
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "answer")
@Table(name = "survey_answer", schema = "surveyor")
public class Answer {
    /**
     * ID made from 2 columns, participant id and question id. It's a PK.
     */
    @EmbeddedId
    private AnswerID answerID;

    /**
     * Content of an answer given by participant.
     */
    @Column(
            name = "answer",
            columnDefinition = "text"
    )
    private String content;
    /**
     * Referenced to chosen pre-made answer chosen by participant.
     */
    @ManyToOne()
    private ClosedAnswer chosen_answer;
    /**
     * Reference to relationship with question that was answered.
     */
    @JsonBackReference(value = "question-answer")
    @ManyToOne()
    @JoinColumn(name = "question_id",
            insertable = false,
            updatable = false)
    private Question question;
    /**
     * Reference to relationship with participant that gave this answer.
     */
    @JsonBackReference(value = "participant-answer")
    @ManyToOne()
    @JoinColumn(name = "participant_id",
            insertable = false,
            updatable = false)
    private Participant participant;
}
