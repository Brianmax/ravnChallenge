package org.ravn.moviescatalogchallenge.service.impl;

import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.UserRequestUpdate;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.entity.Role;
import org.ravn.moviescatalogchallenge.entity.UserEntity;
import org.ravn.moviescatalogchallenge.mapper.UserMapper;
import org.ravn.moviescatalogchallenge.repository.RoleRepository;
import org.ravn.moviescatalogchallenge.repository.UserRepository;
import org.ravn.moviescatalogchallenge.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public ResponseBase<UserResponse> createUser(UserRequest userRequest) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userRequest.getEmail());
        Optional<Role> roleOptional = roleRepository.findByRole(userRequest.getRole());
        List<String> errors = validateInput(userRequest);
        // if the user already exist, or the role does not exist. We do nothing
        if (userOptional.isPresent()) {
            errors.add("User already exists");
        }
        if (roleOptional.isEmpty()) {
            errors.add("Role not found");
        }
        if (!errors.isEmpty()) {
            return new ResponseBase<>(
                    "Error creating user",
                    400,
                    errors,
                    Optional.empty());
        }

        UserEntity userEntity = UserMapper.INSTANCE.userRequestToUser(userRequest, roleOptional.get());
        userRepository.save(userEntity);
        return new ResponseBase<>(
                "User created",
                200,
                new ArrayList<>(),
                Optional.of(UserMapper.INSTANCE.userToUserResponse(userEntity)));
    }

    @Override
    public Optional<UserResponse> updateUser(UserRequestUpdate userRequestUpdate, int id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        Optional<Role> roleOptional = roleRepository.findByRole(userRequestUpdate.getRole());

        if (userOptional.isEmpty() || roleOptional.isEmpty()) {
            return Optional.empty();
        }
        UserEntity userEntity = UserMapper.INSTANCE.userRequestUpdateToUser(
                userRequestUpdate,
                roleOptional.get(),
                userOptional.get()
        );
        userRepository.save(userEntity);
        return Optional.of(UserMapper.INSTANCE.userToUserResponse(userEntity));
    }
    private List<String> validateInput(UserRequest userRequest) {
        List<String> errors = new ArrayList<>();
        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            errors.add("Email is required");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            errors.add("Password is required");
        }
        if (userRequest.getRole() == null || userRequest.getRole().isEmpty()) {
            errors.add("Role is required");
        }
        return errors;
    }
}