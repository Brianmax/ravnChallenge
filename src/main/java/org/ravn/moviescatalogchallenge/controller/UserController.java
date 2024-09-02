package org.ravn.moviescatalogchallenge.controller;

import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody UserRequest userRequest) {
        Optional<UserResponse> userResponse = userService.createUser(userRequest);

        if (userResponse.isEmpty()) {
            return ResponseEntity.badRequest().body("User already exist or role does not exist");
        }

        return ResponseEntity.ok(userResponse.get());
    }
}
