package com.griddynamics.internship.helloworld.specification;

import com.griddynamics.internship.helloworld.domain.Answer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
public class AnswerSpecification implements Specification<Answer>, Serializable {
    private SearchCriteria criteria;

    public Predicate toPredicate(@NonNull Root<Answer> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                if(Objects.equals(criteria.getKey(), "participantId"))query.orderBy(builder.desc(root.get("participantId")));
                else if(Objects.equals(criteria.getKey(), "questionId"))query.orderBy(builder.desc(root.get("questionId")));
                else if(Objects.equals(criteria.getKey(), "surveyId"))query.orderBy(builder.desc(root.get("surveyId")));
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
