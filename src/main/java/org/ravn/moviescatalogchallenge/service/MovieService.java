package org.ravn.moviescatalogchallenge.service;

import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;

public interface MovieService {
    MovieResponse createMovie(MovieRequest movieRequest);
}
