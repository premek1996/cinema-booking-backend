package com.example.cinemabooking.movie.mapper;

import com.example.cinemabooking.movie.dto.CreateMovieRequest;
import com.example.cinemabooking.movie.dto.MovieResponse;
import com.example.cinemabooking.movie.entity.Movie;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovieMapper {

    public static Movie toEntity(CreateMovieRequest request) {
        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .durationMinutes(request.getDurationMinutes())
                .releaseDate(request.getReleaseDate())
                .ageRating(request.getAgeRating())
                .build();
    }

    public static MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .durationMinutes(movie.getDurationMinutes())
                .releaseDate(movie.getReleaseDate())
                .ageRating(movie.getAgeRating())
                .build();
    }

}
