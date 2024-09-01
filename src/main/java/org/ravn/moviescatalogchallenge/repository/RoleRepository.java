package org.ravn.moviescatalogchallenge.repository;

import org.ravn.moviescatalogchallenge.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
