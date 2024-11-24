package com.dev.aes.repository.Query;

import com.dev.aes.entity.DocTypeField;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

public class DocTypeFieldSpecification {
    public static Specification<DocTypeField> getDocTypeFieldPredicate(Long docTypeId, Long pageNumber) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("pageNumber"), pageNumber));
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("docType").get("id"), docTypeId));
            return predicate;
        };
    }
}