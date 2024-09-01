package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, Integer> {
}
