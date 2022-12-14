package com.griddynamics.internship.helloworld.repository;

import com.griddynamics.internship.helloworld.domain.ClosedAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClosedAnswerRepository extends JpaRepository<ClosedAnswer, Long>, JpaSpecificationExecutor<ClosedAnswer> {
}
