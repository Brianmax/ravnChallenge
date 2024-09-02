package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Optional<Movie> findByName(String name);
    Optional<Movie> findBySynopsis(String synopsis);
    @Query("SELECT m FROM Movie m WHERE m.categories IN :category")
    List<Movie> findByCategoriesIn(String category);
}
