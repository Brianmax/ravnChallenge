package org.ravn.moviescatalogchallenge.service;

import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;

import java.util.Optional;

public interface UserService {
    Optional<UserResponse> createUser(UserRequest userRequest);
}
