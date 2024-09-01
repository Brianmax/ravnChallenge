package org.ravn.moviescatalogchallenge.service.impl;

import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.entity.User;
import org.ravn.moviescatalogchallenge.repository.UserRepository;
import org.ravn.moviescatalogchallenge.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

}
