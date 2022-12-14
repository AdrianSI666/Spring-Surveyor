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
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Entity for any created user. Represents basic information about user.
 * Is in a relationship with {@link Survey}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity(name = "user")
@Table(name = "user", schema = "surveyor")
public class User {
    /**
     * ID of the user.
     */
    @Id
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private String id;
    /**
     * Name of the user.
     */
    @Column(
            name = "name",
            length = 30,
            nullable = false
    )
    private String name;
    /**
     * Surname of the user.
     */
    @Column(
            name = "surname",
            length = 30,
            nullable = false
    )
    private String surname;
    /**
     * Surveys created by the user.
     */
    @JsonBackReference(value = "author")
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Survey> createdSurveys;
}
