package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for survey entity.
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long>, JpaSpecificationExecutor<Survey> {
    Page<Survey> findAllByAuthorId(String authorID, Pageable pageable);

    Optional<Survey> findByPasscode(String passcode);

    boolean existsSurveyByPasscode(String passcode);
}
