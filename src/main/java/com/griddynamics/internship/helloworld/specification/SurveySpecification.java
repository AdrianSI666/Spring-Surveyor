package com.griddynamics.internship.helloworld.specification;

import com.griddynamics.internship.helloworld.domain.Survey;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class SurveySpecification implements Specification<Survey>, Serializable {
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull Root<Survey> root,
                                 @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder builder) {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (criteria.getKey().contains("_")) {
                String[] value = criteria.getKey().split("_");
                Join<Object, Object> listJoin = root.join(value[0], JoinType.INNER);
                return builder.equal(listJoin.get(value[1]), criteria.getValue());
            }
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
