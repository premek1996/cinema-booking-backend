package com.example.cinemabooking.movie.dto;

import com.example.cinemabooking.movie.entity.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class CreateMovieRequest {

    @NotBlank
    String title;

    @NotBlank
    String description;

    @NotBlank
    String genre;

    @Min(1)
    int durationMinutes;

    @NotNull
    @PastOrPresent
    LocalDate releaseDate;

    @NotNull
    AgeRating ageRating;

}
