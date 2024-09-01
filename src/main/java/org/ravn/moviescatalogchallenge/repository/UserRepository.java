package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
