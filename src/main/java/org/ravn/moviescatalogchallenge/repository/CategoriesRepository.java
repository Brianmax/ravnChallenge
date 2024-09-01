package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categorie, Integer> {
}
