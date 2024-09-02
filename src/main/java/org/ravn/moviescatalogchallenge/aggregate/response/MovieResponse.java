package org.ravn.moviescatalogchallenge.aggregate.response;

import lombok.Getter;
import lombok.Setter;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.UserRequest;

@Getter
@Setter
public class MovieResponse extends MovieRequest {
    private UserResponse user;
}
