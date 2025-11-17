package com.example.cinemabooking.movie.web;

import com.example.cinemabooking.movie.dto.CreateMovieRequest;
import com.example.cinemabooking.movie.dto.MovieResponse;
import com.example.cinemabooking.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    MovieResponse getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MovieResponse createMovie(@RequestBody @Valid CreateMovieRequest createMovieRequest) {
        return movieService.createMovie(createMovieRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

}
