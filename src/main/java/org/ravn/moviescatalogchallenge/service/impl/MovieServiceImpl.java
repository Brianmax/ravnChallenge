package org.ravn.moviescatalogchallenge.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;

import org.ravn.moviescatalogchallenge.aggregate.request.BaseMovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieCreateRequest;
import org.ravn.moviescatalogchallenge.aggregate.request.MovieUpdateRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.ravn.moviescatalogchallenge.entity.Category;
import org.ravn.moviescatalogchallenge.entity.Movie;
import org.ravn.moviescatalogchallenge.entity.UserEntity;
import org.ravn.moviescatalogchallenge.mapper.MovieMapper;
import org.ravn.moviescatalogchallenge.mapper.UserMapper;
import org.ravn.moviescatalogchallenge.repository.CategoriesRepository;
import org.ravn.moviescatalogchallenge.repository.MovieRepository;
import org.ravn.moviescatalogchallenge.repository.UserRepository;
import org.ravn.moviescatalogchallenge.repository.specification.MovieSpecification;
import org.ravn.moviescatalogchallenge.service.JwtService;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final JwtService jwtService;

    public MovieServiceImpl(MovieRepository movieRepository, CategoriesRepository categoriesRepository, UserRepository userRepository, HttpServletRequest request, JwtService jwtService) {
        this.movieRepository = movieRepository;
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
        this.request = request;
        this.jwtService = jwtService;
    }
    @Override
    public ResponseBase<MovieResponse> createMovie(MovieCreateRequest movieCreateRequest) {
        Optional<Movie> movieOptional = movieRepository.findByName(movieCreateRequest.getName());
        List<Category> categories = categoriesRepository.findByNames(movieCreateRequest.getCategories());
        Optional<UserEntity> userOptional = userRepository.findById(movieCreateRequest.getUserId());
        List<String> categoriesThatNotExists = getCategoriesThatNotExists(categories, movieCreateRequest.getCategories());
        List<String> errors = validateInputCreate(movieCreateRequest);
        if (movieOptional.isPresent()) {
            errors.add("Movie already exists");
        }
        if (userOptional.isEmpty()) {
            errors.add("User not found");
        }
        if (categories.isEmpty() || categories.size() != movieCreateRequest.getCategories().size()) {
            errors.add("Categories not found: " + categoriesThatNotExists);
        }

        if (!errors.isEmpty()) {
            return new ResponseBase<>(
                    "Error creating movie",
                    400,
                    errors,
                    Optional.empty());
        }
        Movie movie = MovieMapper.INSTANCE.movieRequestToMovie(
                movieCreateRequest,
                categories,
                userOptional.get(),
                new Date(System.currentTimeMillis()));
        movieRepository.save(movie);
        MovieResponse movieResponse = MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                movieCreateRequest.getCategories(),
                movie.getUserEntity().getEmail());

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
                        movie.getUserEntity().getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseBase<MovieResponse> updateMovie(MovieUpdateRequest movieUpdateRequest, String movieName) {
        List<Category> categories = categoriesRepository.findByNames(movieUpdateRequest.getCategories());
        List<String> categoriesThatNotExists = getCategoriesThatNotExists(categories, movieUpdateRequest.getCategories());
        Optional<Movie> movieOptional = movieRepository.findByName(movieName);
        List<String> errors = validateInput(movieUpdateRequest);
        if (movieRepository.existsByName(movieUpdateRequest.getName())) {
            errors.add("Movie name already exists");
        }
        if (categories.isEmpty() || categories.size() != movieUpdateRequest.getCategories().size()) {
            errors.add("Categories not found: " + categoriesThatNotExists);
        }
        if (movieOptional.isEmpty()) {
            errors.add("Movie: " + movieName + " that you want to update does not exist");
        }

        if ( !errors.isEmpty())
        {
            return new ResponseBase<>(
                    "Error updating movie",
                    400,
                    errors,
                    Optional.empty());
        }
        String token = request.getHeader("Authorization");
        token = token.substring(7);

        Movie movie = movieOptional.get();
        MovieMapper.INSTANCE.updateMovieFromMovieUpdateRequest(
                movieUpdateRequest,
                movie,
                categories,
                new Date(System.currentTimeMillis()),
                jwtService.extractUsername(token));
        movieRepository.save(movie);

        MovieResponse movieResponse = MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                movieUpdateRequest.getCategories(),
                movie.getUserEntity().getEmail());

        return new ResponseBase<>(
                "Movie updated successfully",
                200,
                errors,
                Optional.of(movieResponse)
        );
    }

    @Override
    public ResponseBase<MovieResponse> deleteMovie(String movieName) {

        Optional<Movie> movieOptional = movieRepository.findByName(movieName);
        if (movieOptional.isEmpty())
        {
            return new ResponseBase<>(
                    "Error deleting movie",
                    400,
                    List.of("Movie not found"),
                    Optional.empty());
        }
        String token = request.getHeader("Authorization").substring(7);
        Movie movie = movieOptional.get();
        movie.setDeleted(true);
        movie.setDeletedAt(new Date(System.currentTimeMillis()));
        movie.setDeletedBy(jwtService.extractUsername(token));
        MovieResponse movieResponse = MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                getCategoriesNames(movie),
                movie.getUserEntity().getEmail());
        movieRepository.save(movie);
        return new ResponseBase<>(
                "Movie deleted successfully",
                200,
                List.of(),
                Optional.of(movieResponse)
        );
    }

    @Override
    public Page<MovieResponse> searchMovies(String keyword, String categoryName, int releaseYear, Pageable pageable) {
        Specification<Movie> spec1 = MovieSpecification.hasReleaseYear(releaseYear);
        Specification<Movie> specification = Specification
                .where(MovieSpecification.hasCategory(keyword))
                .or(MovieSpecification.hasCategory(categoryName))
                .or(MovieSpecification.hasReleaseYear(releaseYear));
        List<Movie> movies = movieRepository.findAll(specification, pageable).getContent();
        if(movies.isEmpty()) {
            return Page.empty();
        }
        return movieRepository.findAll(specification, pageable)
                .map(movie -> MovieMapper.INSTANCE.movieToMovieResponse(
                        movie,
                        getCategoriesNames(movie),
                        movie.getUserEntity().getEmail()));
    }




    private List<String> getCategoriesNames(Movie movie) {
        return movie.getCategories().stream().map(Category::getName).collect(Collectors.toList());
    }

    private List<String> getCategoriesThatNotExists(List<Category> categories, List<String> categoriesRequest) {
        return categoriesRequest.stream()
                .filter(category -> categories.stream().noneMatch(categorie -> categorie.getName().equals(category)))
                .collect(Collectors.toList());
    }

    private List<String> validateInput(BaseMovieRequest movieRequest) {
        List<String> errors = new ArrayList<>();
        if (movieRequest.getName() == null || movieRequest.getName().isEmpty()) {
            errors.add("Name is required");
        }
        if (movieRequest.getReleaseYear() == 0) {
            errors.add("Release date is required");
        }
        if (movieRequest.getCategories() == null || movieRequest.getCategories().isEmpty()) {
            errors.add("Categories are required");
        }
        return errors;
    }
    private List<String> validateInputCreate(MovieCreateRequest movieCreateRequest)
    {
        List<String> errors = validateInput(movieCreateRequest);

        if (movieCreateRequest.getUserId() == 0) {
            errors.add("User id is required");
        }
        return errors;
    }
}
