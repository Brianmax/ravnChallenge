package org.ravn.moviescatalogchallenge.service.impl;

import com.sun.source.tree.OpensTree;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.entity.Categorie;
import org.ravn.moviescatalogchallenge.entity.Movie;
import org.ravn.moviescatalogchallenge.mapper.MovieMapper;
import org.ravn.moviescatalogchallenge.repository.CategoriesRepository;
import org.ravn.moviescatalogchallenge.repository.MovieRepository;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<Movie> movieOptional = movieRepository.findByName(movieRequest.getName());
        List<Categorie> categories = categoriesRepository.findByNames(movieRequest.getCategories());

        if (movieOptional.isPresent() || categories.isEmpty()) {
            return null;
        }
        Movie movie = MovieMapper.INSTANCE.movieRequestToMovie(movieRequest, categories);
        movieRepository.save(movie);
        return MovieMapper.INSTANCE.movieToMovieResponse(movie, movieRequest.getCategories());
    }
}
