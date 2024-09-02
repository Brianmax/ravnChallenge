package org.ravn.moviescatalogchallenge.service.impl;

import com.sun.source.tree.OpensTree;
import java.util.Collections;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.UserResponse;
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
    public MovieResponse createMovie(MovieRequest movieRequest) {
        Optional<Movie> movieOptional = movieRepository.findByName(movieRequest.getName());
        List<Categorie> categories = categoriesRepository.findByNames(movieRequest.getCategories());
        Optional<User> userOptional = userRepository.findById(movieRequest.getUserId());
        if (movieOptional.isPresent() || categories.isEmpty() || userOptional.isEmpty()) {
            return null;
        }
        Movie movie = MovieMapper.INSTANCE.movieRequestToMovie(movieRequest, categories, userOptional.get());
        movieRepository.save(movie);
        return MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                movieRequest.getCategories(),
                UserMapper.INSTANCE.userToUserResponse(userOptional.get()));
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

    private List<String> getCategoriesNames(Movie movie) {
        return movie.getCategories().stream().map(Categorie::getName).collect(Collectors.toList());
    }
}
