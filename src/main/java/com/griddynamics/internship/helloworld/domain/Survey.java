package com.griddynamics.internship.helloworld.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

/**
 * Entity for any survey created by the user. Represents basic information about survey.
 * Is in a relationship with {@link Question}, {@link User},
 * {@link Participant}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity(name = "survey")
@Table(name = "survey", schema = "surveyor")
@NamedEntityGraph(
        name = "Survey.List",
        attributeNodes = {@NamedAttributeNode(value = "questions", subgraph = "Survey.Question")},
        subgraphs = {
                @NamedSubgraph(name = "Survey.Question",
                        attributeNodes = @NamedAttributeNode(value = "answers"))
        }
)
public class Survey {
    /**
     * ID of the survey.
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
     * Name of the survey.
     */
    @Column(
            name = "name",
            length = 30,
            nullable = false
    )
    private String name;
    /**
     * Description of the survey.
     */
    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "text"
    )
    private String description;
    /**
     * Passcode used to join the survey.
     */
    @Column(
            name = "passcode",
            length = 8
    )
    private String passcode;
    /**
     * Time set for each of the questions in the survey.
     */
    @Column(
            name = "duration",
            nullable = false
    )
    private LocalTime duration;
    /**
     * Questions belonging to the survey.
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Question> questions;
    /**
     * User that created the survey.
     */
    @ManyToOne()
    @JoinColumn(name = "author")
    private User author;
    /**
     * Participants that take part in the survey.
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<Participant> participants;
    /**
     * Flag used to determine if the survey has started.
     */
    @Column(
            name = "survey_started",
            nullable = false,
            columnDefinition = "boolean default false"
    )
    private boolean started;
    /**
     * Time at with the survey has started.
     */
    @Column(
            name = "time_started"
    )
    private Instant dateTimeStarted;
}
