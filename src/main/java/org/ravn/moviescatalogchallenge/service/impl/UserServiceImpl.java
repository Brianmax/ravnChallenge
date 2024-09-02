package org.ravn.moviescatalogchallenge.service.impl;

import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.UserRequestUpdate;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.entity.Role;
import org.ravn.moviescatalogchallenge.entity.User;
import org.ravn.moviescatalogchallenge.mapper.UserMapper;
import org.ravn.moviescatalogchallenge.repository.RoleRepository;
import org.ravn.moviescatalogchallenge.repository.UserRepository;
import org.ravn.moviescatalogchallenge.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<UserResponse> createUser(UserRequest userRequest) {
        Optional<User> userOptional = userRepository.findByEmail(userRequest.getEmail());
        Optional<Role> roleOptional = roleRepository.findByRole(userRequest.getRole());
        // if the user already exist, or the role does not exist. We do nothing
        if (userOptional.isPresent() || roleOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = UserMapper.INSTANCE.userRequestToUser(userRequest, roleOptional.get());
        userRepository.save(user);
        return Optional.of(UserMapper.INSTANCE.userToUserResponse(user));
    }

    @Override
    public Optional<UserResponse> updateUser(UserRequestUpdate userRequestUpdate, int id) {
        Optional<User> userOptional = userRepository.findById(id);
        Optional<Role> roleOptional = roleRepository.findByRole(userRequestUpdate.getRole());

        if (userOptional.isEmpty() || roleOptional.isEmpty()) {
            return Optional.empty();
        }
        User user = UserMapper.INSTANCE.userRequestUpdateToUser(
                userRequestUpdate,
                roleOptional.get(),
                userOptional.get()
        );
        userRepository.save(user);
        return Optional.of(UserMapper.INSTANCE.userToUserResponse(user));
    }
}