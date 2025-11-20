package com.example.cinemabooking.movie.dto;

import com.example.cinemabooking.movie.entity.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMovieRequest {

    @Builder.Default
    private Optional<@NotBlank String> title = Optional.empty();

    @Builder.Default
    private Optional<@NotBlank String> description = Optional.empty();

    @Builder.Default
    private Optional<@NotBlank String> genre = Optional.empty();

    @Builder.Default
    private Optional<@Min(1) Integer> durationMinutes = Optional.empty();

    @Builder.Default
    private Optional<@PastOrPresent LocalDate> releaseDate = Optional.empty();

    @Builder.Default
    private Optional<AgeRating> ageRating = Optional.empty();

}
