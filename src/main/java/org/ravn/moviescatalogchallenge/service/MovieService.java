package org.ravn.moviescatalogchallenge.service;

import org.ravn.moviescatalogchallenge.aggregate.request.BaseMovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {
    ResponseBase<MovieResponse> createMovie(BaseMovieRequest movieCreateRequest);
    List<MovieResponse> getAllMovies(int page, int size);
    ResponseBase<MovieResponse> updateMovie(BaseMovieRequest movieUpdateRequest, String movieName);
    ResponseBase<MovieResponse> deleteMovie(String movieName);
    Page<MovieResponse> searchMovies(String keyword, String categoryName, Integer releaseYear, Pageable pageable);
}
