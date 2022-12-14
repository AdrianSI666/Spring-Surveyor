package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.domain.Answer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for answer entity.
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long>, JpaSpecificationExecutor<Answer> {

    /**
     * Seeks all the answers belonging to the survey with the help of the id of the survey.
     */
    @EntityGraph(value = "Survey.List")
    @Query(value = """
            Select answer.*
            from surveyor.survey survey
            join surveyor.survey_questions question on survey.id = question.survey_id
            join surveyor.survey_answer answer on question.id = answer.question_id
            join surveyor.survey_participants participant on answer.participant_id = participant.id
            where survey.id = ?1
            order by question.id;
            """, nativeQuery = true)
    List<Answer> findAllBySurvey(Long id);
}
