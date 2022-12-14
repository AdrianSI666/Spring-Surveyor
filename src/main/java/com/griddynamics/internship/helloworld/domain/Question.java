package com.griddynamics.internship.helloworld.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Entity for any questions created by the user in a survey. Represents basic information about question.
 * Is in relationship with {@link Answer}, {@link Survey}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "question")
@Table(name = "survey_questions", schema = "surveyor")
public class Question {
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
     * Name of the question.
     */
    @Column(
            name = "name",
            length = 30,
            nullable = false
    )
    private String name;
    /**
     * Body of the question itself.
     */
    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "text"
    )
    private String content;
    /**
     * Answers to the question.
     */
    @JsonBackReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;
    /**
     * Survey that the question belongs to.
     */
    @ManyToOne
    private Survey survey;
    /**
     * List of pre-made answers
     */
    @JsonBackReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<ClosedAnswer> closedAnswers;
}
