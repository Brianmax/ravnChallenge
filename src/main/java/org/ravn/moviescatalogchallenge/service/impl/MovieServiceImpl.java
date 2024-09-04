package org.ravn.moviescatalogchallenge.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;

import org.ravn.moviescatalogchallenge.aggregate.request.BaseMovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.ravn.moviescatalogchallenge.config.RedisService;
import org.ravn.moviescatalogchallenge.entity.Category;
import org.ravn.moviescatalogchallenge.entity.Movie;
import org.ravn.moviescatalogchallenge.entity.UserEntity;
import org.ravn.moviescatalogchallenge.mapper.MovieMapper;
import org.ravn.moviescatalogchallenge.repository.CategoriesRepository;
import org.ravn.moviescatalogchallenge.repository.MovieRepository;
import org.ravn.moviescatalogchallenge.repository.UserRepository;
import org.ravn.moviescatalogchallenge.repository.specification.MovieSpecification;
import org.ravn.moviescatalogchallenge.service.MinioService;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.ravn.moviescatalogchallenge.utils.Utils.*;

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
    private final RedisService redisService;
    private final MinioService minioService;

    public MovieServiceImpl(MovieRepository movieRepository, CategoriesRepository categoriesRepository, UserRepository userRepository, HttpServletRequest request, RedisService redisService, MinioService minioService) {
        this.movieRepository = movieRepository;
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
        this.request = request;
        this.redisService = redisService;
        this.minioService = minioService;
    }
    @Override
    public ResponseBase<MovieResponse> createMovie(BaseMovieRequest movieCreateRequest) {
        String userLogged = getLoggedUser(request);
        Optional<Movie> movieOptional = movieRepository.findByName(movieCreateRequest.getName());
        List<Category> categories = categoriesRepository.findByNames(movieCreateRequest.getCategories());
        Optional<UserEntity> userOptional = userRepository.findByEmail(userLogged);
        List<String> categoriesThatNotExists = getCategoriesThatNotExists(categories, movieCreateRequest.getCategories());
        List<String> errors = validateInput(movieCreateRequest);
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
                userLogged,
                minioService.getPresignedUrl(movie.getPoster()));

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
                        movie.getUserEntity().getEmail(),
                        minioService.getPresignedUrl(movie.getPoster())))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseBase<MovieResponse> updateMovie(BaseMovieRequest movieUpdateRequest, String movieName) {
        String userLogged = getLoggedUser(request);
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
        Movie movie = movieOptional.get();
        MovieMapper.INSTANCE.updateMovieFromMovieUpdateRequest(
                movieUpdateRequest,
                movie,
                categories,
                new Date(System.currentTimeMillis()),
                userLogged);
        movieRepository.save(movie);

        MovieResponse movieResponse = MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                movieUpdateRequest.getCategories(),
                userLogged,
                minioService.getPresignedUrl(movie.getPoster()));

        return new ResponseBase<>(
                "Movie updated successfully",
                200,
                errors,
                Optional.of(movieResponse)
        );
    }

    @Override
    public ResponseBase<MovieResponse> deleteMovie(String movieName) {
        String userLogged = getLoggedUser(request);
        Optional<Movie> movieOptional = movieRepository.findByName(movieName);
        if (movieOptional.isEmpty())
        {
            return new ResponseBase<>(
                    "Error deleting movie",
                    400,
                    List.of("Movie not found"),
                    Optional.empty());
        }
        Movie movie = movieOptional.get();
        movie.setDeleted(true);
        movie.setDeletedAt(new Date(System.currentTimeMillis()));
        movie.setDeletedBy(userLogged);
        MovieResponse movieResponse = MovieMapper.INSTANCE.movieToMovieResponse(
                movie,
                getCategoriesNames(movie),
                movie.getUserEntity().getEmail(),
                minioService.getPresignedUrl(movie.getPoster()));
        movieRepository.save(movie);
        return new ResponseBase<>(
                "Movie deleted successfully",
                200,
                List.of(),
                Optional.of(movieResponse)
        );
    }

    @Override
    public List<MovieResponse> searchMovies(String keyword, String categoryName, Integer releaseYear, Pageable pageable) {
        Specification<Movie> specification = Specification
                .where(MovieSpecification.hasCategory(keyword))
                .or(MovieSpecification.hasCategory(categoryName))
                .or(MovieSpecification.hasReleaseYear(releaseYear));
        List<Movie> movies = movieRepository.findAll(specification, pageable).getContent();
        if(movies.isEmpty()) {
            return Collections.emptyList();
        }
        String key = request.getQueryString();
        List<MovieResponse> movieResponses = movies.stream()
                .map(movie -> MovieMapper.INSTANCE.movieToMovieResponse(
                        movie,
                        getCategoriesNames(movie),
                        movie.getUserEntity().getEmail(),
                        minioService.getPresignedUrl(movie.getPoster())))
                .collect(Collectors.toList());
        String redisData = convertToJson(movieResponses);
        redisService.saveKeyValue(key, redisData, 5);
        return movieResponses;
    }

    @Override
    public ResponseBase<String> uploadImage(String movieName, MultipartFile file) {
        Optional<Movie> movieOptional = movieRepository.findByName(movieName);
        List <String> errors = new ArrayList<>();
        if (movieOptional.isEmpty()) {
            errors.add("Movie not found");
        }
        String imageName = minioService.uploadImage(file);
        if (imageName == null) {
            errors.add("Error uploading image");
        }

        if (movieOptional.isPresent() && imageName != null) {
            Movie movie = movieOptional.get();
            movie.setPoster(imageName);
            movieRepository.save(movie);
            return new ResponseBase<>(
                    "Image uploaded successfully",
                    200,
                    errors,
                    Optional.of(imageName)
            );
        }
        return new ResponseBase<>(
                "Error uploading image",
                400,
                errors,
                Optional.empty()
        );
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
}
