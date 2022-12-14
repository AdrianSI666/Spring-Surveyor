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
 * Entity for any participant that joined some survey. Represents basic information about the participant.
 * Is in a relationship with {@link Survey}, {@link Answer}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "participant")
@Table(name = "survey_participants", schema = "surveyor")
public class Participant {
    /**
     * ID of the participant.
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
     * Nickname of the participant.
     */
    @Column(
            name = "nick",
            nullable = false,
            length = 50

    )
    private String nick;
    /**
     * Survey that the participant joined.
     */
    @JsonBackReference(value = "survey-participant")
    @ManyToOne()
    private Survey survey;
    /**
     * Answers that the participant submitted.
     */
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<Answer> answers;
}
