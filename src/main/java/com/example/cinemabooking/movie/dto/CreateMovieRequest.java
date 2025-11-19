package com.example.cinemabooking.movie.dto;

import com.example.cinemabooking.movie.entity.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
