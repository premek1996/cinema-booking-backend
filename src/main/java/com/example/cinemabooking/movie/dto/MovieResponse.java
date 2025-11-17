package com.example.cinemabooking.movie.dto;

import com.example.cinemabooking.movie.entity.AgeRating;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class MovieResponse {

    Long id;
    String title;
    String description;
    String genre;
    int durationMinutes;
    LocalDate releaseDate;
    AgeRating ageRating;

}
