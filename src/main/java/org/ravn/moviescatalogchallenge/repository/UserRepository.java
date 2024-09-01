package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
