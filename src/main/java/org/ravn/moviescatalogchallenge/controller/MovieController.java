package org.ravn.moviescatalogchallenge.controller;

import org.ravn.moviescatalogchallenge.aggregate.request.BaseMovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.aggregate.response.ResponseBase;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/admin/save")
    public ResponseBase<MovieResponse> saveMovie(@RequestBody BaseMovieRequest movieCreateRequest) {
        return movieService.createMovie(movieCreateRequest);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllMovies(@RequestParam int page, @RequestParam int size) {
        List<MovieResponse> movieResponses = movieService.getAllMovies(page, size);

        if (movieResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movieResponses);
    }
    @GetMapping("/search")
    public ResponseBase<Page<MovieResponse>> getMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseYear") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<MovieResponse> movies = movieService.searchMovies(keyword, categoryName, releaseYear, pageable);
        return new ResponseBase<>(
                "Movies retrieved successfully",
                200,
                new ArrayList<>(),
                Optional.of(movies));
    }
    @PutMapping("/admin/update")
    public ResponseBase<MovieResponse> updateMovie(@RequestBody BaseMovieRequest movieUpdateRequest, @RequestParam String movieName) {
        return movieService.updateMovie(movieUpdateRequest, movieName);
    }

    @DeleteMapping("/admin/delete")
    public ResponseBase<MovieResponse> deleteMovie(@RequestParam String movieName) {
        return movieService.deleteMovie(movieName);
    }
}
