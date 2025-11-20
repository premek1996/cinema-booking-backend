package com.example.cinemabooking.movie.service;

import com.example.cinemabooking.movie.dto.CreateMovieRequest;
import com.example.cinemabooking.movie.dto.MovieResponse;
import com.example.cinemabooking.movie.dto.UpdateMovieRequest;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.mapper.MovieMapper;
import com.example.cinemabooking.movie.repository.MovieRepository;
import com.example.cinemabooking.movie.service.exception.MovieAlreadyExistsException;
import com.example.cinemabooking.movie.service.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = getMovieOrThrow(id);
        return MovieMapper.toResponse(movie);
    }

    @Transactional(readOnly = true)
    public Movie getMovieOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Transactional
    public MovieResponse createMovie(CreateMovieRequest createMovieRequest) {
        validateUniqueTitle(createMovieRequest.getTitle(), null);
        Movie movie = MovieMapper.toEntity(createMovieRequest);
        return MovieMapper.toResponse(movieRepository.save(movie));
    }

    private void validateUniqueTitle(String title, Long currentMovieId) {
        movieRepository.findByTitle(title)
                .filter(movie -> !movie.getId().equals(currentMovieId))
                .ifPresent(movie -> {
                    throw new MovieAlreadyExistsException("Movie title already exists");
                });
    }

    @Transactional
    public MovieResponse updateMovie(Long id, UpdateMovieRequest request) {
        Movie movie = getMovieOrThrow(id);
        request.getTitle().ifPresent(newTitle -> {
            validateUniqueTitle(newTitle, id);
            movie.setTitle(newTitle);
        });
        request.getDescription().ifPresent(movie::setDescription);
        request.getGenre().ifPresent(movie::setGenre);
        request.getDurationMinutes().ifPresent(movie::setDurationMinutes);
        request.getReleaseDate().ifPresent(movie::setReleaseDate);
        request.getAgeRating().ifPresent(movie::setAgeRating);
        return MovieMapper.toResponse(movie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = getMovieOrThrow(id);
        movieRepository.delete(movie);
    }

}
