package org.ravn.moviescatalogchallenge.service;

import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.UserRequestUpdate;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserResponse> createUser(UserRequest userRequest);
    Optional<UserResponse> updateUser(UserRequestUpdate userRequestUpdate, int id);
}
