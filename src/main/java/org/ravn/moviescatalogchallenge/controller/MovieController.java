package org.ravn.moviescatalogchallenge.controller;

import org.ravn.moviescatalogchallenge.aggregate.request.MovieRequest;
import org.ravn.moviescatalogchallenge.aggregate.response.MovieResponse;
import org.ravn.moviescatalogchallenge.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveMovie(@RequestBody MovieRequest movieRequest) {
        MovieResponse movieResponse = movieService.createMovie(movieRequest);

        if (movieResponse == null) {
            return ResponseEntity.badRequest().body("Movie already exist or categories do not exist");
        }
        return ResponseEntity.ok(movieResponse);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllMovies(@RequestParam int page, @RequestParam int size) {
        List<MovieResponse> movieResponses = movieService.getAllMovies(page, size);

        if (movieResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movieResponses);
    }

    @GetMapping("/category")
    public ResponseEntity<?> getMoviesByCategory(@RequestParam String category, @RequestParam int page, @RequestParam int size) {
        List<MovieResponse> movieResponses = movieService.getMoviesByCategory(category, page, size);

        if (movieResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movieResponses);
    }
}
