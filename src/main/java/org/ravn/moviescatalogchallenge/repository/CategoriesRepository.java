package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Categorie, Integer> {
    @Query("SELECT c FROM Categorie c WHERE c.name IN :names")
    List<Categorie> findByNames(List<String> names);
}
