package org.ravn.moviescatalogchallenge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
import org.ravn.moviescatalogchallenge.entity.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    User userRequestToUser(UserRequest userRequest);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "role.role", target = "role")
    UserResponse userToUserResponse(User user);
}
