package org.ravn.moviescatalogchallenge.service;

import org.ravn.moviescatalogchallenge.aggregate.request.MovieCreateRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieUpdateRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {
    ResponseBase<MovieResponse> createMovie(MovieCreateRequest movieCreateRequest);
    List<MovieResponse> getAllMovies(int page, int size);
    ResponseBase<MovieResponse> updateMovie(MovieUpdateRequest movieUpdateRequest, String movieName);
    ResponseBase<MovieResponse> deleteMovie(String movieName);
    Page<MovieResponse> searchMovies(String keyword, String categoryName, int releaseYear, Pageable pageable);
}
