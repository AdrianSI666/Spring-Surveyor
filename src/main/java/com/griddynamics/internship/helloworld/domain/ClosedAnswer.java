package com.griddynamics.internship.helloworld.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "closedAnswer")
@Table(name = "answer", schema = "surveyor")
public class ClosedAnswer {
    /**
     * ID of the question.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;
    /**
     * Content of pre-made answer.
     */
    @Column(
            name = "answer",
            columnDefinition = "text",
            nullable = false
    )
    private String content;
    /**
     * Value of answer to check correct answer
     */
    @Column(
            name = "value",
            nullable = false
    )
    private Integer value;
    /**
     * Reference to question
     */
    @JsonBackReference(value = "question-closed-answer")
    @ManyToOne()
    private Question question;
    /**
     * List of answers in where it was used by participant.
     */
    @JsonBackReference
    @OneToMany(mappedBy = "chosen_answer")
    private List<Answer> answers;
}
