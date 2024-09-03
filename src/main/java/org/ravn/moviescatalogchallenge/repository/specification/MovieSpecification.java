package org.ravn.moviescatalogchallenge.repository.specification;

import org.ravn.moviescatalogchallenge.entity.Category;
import org.ravn.moviescatalogchallenge.entity.Movie;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
public class MovieSpecification {
    public static Specification<Movie> hasNameOrSynopsis(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("synopsis")), likePattern)
            );
        };
    }

    public static Specification<Movie> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Movie, Category> categories = root.join("categories");
            return criteriaBuilder.equal(categories.get("name"), categoryName);
        };
    }

    public static Specification<Movie> hasReleaseYear(Integer releaseYear) {
        return (root, query, criteriaBuilder) -> {
            if (releaseYear == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("releaseYear"), releaseYear);
        };
    }
}
