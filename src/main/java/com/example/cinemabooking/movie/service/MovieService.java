package com.example.cinemabooking.movie.service;

import com.example.cinemabooking.movie.dto.CreateMovieRequest;
import com.example.cinemabooking.movie.dto.MovieResponse;
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

    private Movie getMovieOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Transactional
    public MovieResponse createMovie(CreateMovieRequest createMovieRequest) {
        validateUniqueTitle(createMovieRequest.getTitle());
        Movie movie = MovieMapper.toEntity(createMovieRequest);
        return MovieMapper.toResponse(movieRepository.save(movie));
    }

    private void validateUniqueTitle(String title) {
        if (movieRepository.findByTitle(title).isPresent()) {
            throw new MovieAlreadyExistsException(title);
        }
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = getMovieOrThrow(id);
        movieRepository.delete(movie);
    }

}
