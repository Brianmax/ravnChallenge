package org.ravn.moviescatalogchallenge.service;

import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;

import java.util.List;

public interface MovieService {
    MovieResponse createMovie(MovieRequest movieRequest);
    List<MovieResponse> getAllMovies(int page, int size);
}
