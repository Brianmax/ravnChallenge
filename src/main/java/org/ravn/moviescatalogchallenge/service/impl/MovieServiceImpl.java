package org.ravn.moviescatalogchallenge.service.impl;

import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.repository.CategoriesRepository;
import org.ravn.moviescatalogchallenge.repository.MovieRepository;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final CategoriesRepository categoriesRepository;

    public MovieServiceImpl(MovieRepository movieRepository, CategoriesRepository categoriesRepository) {
        this.movieRepository = movieRepository;
        this.categoriesRepository = categoriesRepository;
    }
    @Override
    public MovieResponse createMovie(MovieRequest movieRequest) {
        return null;
    }
}
