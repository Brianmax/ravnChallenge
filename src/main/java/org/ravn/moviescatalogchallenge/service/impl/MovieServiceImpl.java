package org.ravn.moviescatalogchallenge.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.ravn.moviescatalogchallenge.entity.Categorie;
import org.ravn.moviescatalogchallenge.entity.Movie;
import org.ravn.moviescatalogchallenge.entity.User;
import org.ravn.moviescatalogchallenge.mapper.MovieMapper;
import org.ravn.moviescatalogchallenge.mapper.UserMapper;
import org.ravn.moviescatalogchallenge.repository.CategoriesRepository;
import org.ravn.moviescatalogchallenge.repository.MovieRepository;
import org.ravn.moviescatalogchallenge.repository.UserRepository;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;

    public MovieServiceImpl(MovieRepository movieRepository, CategoriesRepository categoriesRepository, UserRepository userRepository) {
        this.movieRepository = movieRepository;
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
    }
    @Override
    public ResponseBase<MovieResponse> createMovie(MovieRequest movieRequest) {
        Optional<Movie> movieOptional = movieRepository.findByName(movieRequest.getName());
        List<Categorie> categories = categoriesRepository.findByNames(movieRequest.getCategories());
        Optional<User> userOptional = userRepository.findById(movieRequest.getUserId());
        List<String> categoriesThatNotExists = getCategoriesThatNotExists(categories, movieRequest);
        List<String> errors = validateInput(movieRequest);
        if (movieOptional.isPresent()) {
            errors.add("Movie already exists");
        }
        if (userOptional.isEmpty()) {
            errors.add("User not found");
        }
        if (categories.isEmpty() || categories.size() != movieRequest.getCategories().size()) {
            errors.add("Categories not found: " + categoriesThatNotExists);
        }

        if (!errors.isEmpty()) {
            return new ResponseBase<>(
                    "Error creating movie",
                    400,
                    errors,
                    Optional.empty());
        }
        Movie movie = MovieMapper.INSTANCE.movieRequestToMovie(movieRequest, categories, userOptional.get());
        movieRepository.save(movie);
        MovieResponse movieResponse = MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                movieRequest.getCategories(),
                UserMapper.INSTANCE.userToUserResponse(userOptional.get()));

        return new ResponseBase<>(
                "Movie created successfully",
                200,
                errors,
                Optional.of(movieResponse));
    }

    @Override
    public List<MovieResponse> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Movie> movies = movieRepository.findAll(pageable).getContent();

        if(movies.isEmpty()) {
            return Collections.emptyList();
        }
        return movies.stream()
                .map(movie -> MovieMapper.INSTANCE.movieToMovieResponse(
                        movie,
                        getCategoriesNames(movie),
                        UserMapper.INSTANCE.userToUserResponse(movie.getUser())))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getMoviesByCategory(String category, int page, int size) {
        List<Movie> movies = movieRepository.findByCategoriesIn(category);
        if(movies.isEmpty()) {
            return Collections.emptyList();
        }
        return movies.stream()
                .map(movie -> MovieMapper.INSTANCE.movieToMovieResponse(
                        movie,
                        getCategoriesNames(movie),
                        UserMapper.INSTANCE.userToUserResponse(movie.getUser())))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponse> getMoviesByYear(int year, int page, int size) {
        return List.of();
    }

    private List<String> getCategoriesNames(Movie movie) {
        return movie.getCategories().stream().map(Categorie::getName).collect(Collectors.toList());
    }

    private List<String> getCategoriesThatNotExists(List<Categorie> categories, MovieRequest movieRequest) {
        return movieRequest.getCategories().stream()
                .filter(category -> categories.stream().noneMatch(categorie -> categorie.getName().equals(category)))
                .collect(Collectors.toList());
    }

    private List<String> validateInput(MovieRequest movieRequest) {
        List<String> errors = new ArrayList<>();
        if (movieRequest.getName() == null || movieRequest.getName().isEmpty()) {
            errors.add("Name is required");
        }
        if (movieRequest.getReleaseDate() == null) {
            errors.add("Release date is required");
        }
        if (movieRequest.getCategories() == null || movieRequest.getCategories().isEmpty()) {
            errors.add("Categories are required");
        }
        if (movieRequest.getUserId() == 0) {
            errors.add("User is required");
        }
        return errors;
    }
}
