package com.example.cinemabooking.movie.mapper;

import com.example.cinemabooking.movie.dto.CreateMovieRequest;
import com.example.cinemabooking.movie.dto.MovieResponse;
import com.example.cinemabooking.movie.entity.AgeRating;
import com.example.cinemabooking.movie.entity.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MovieMapperTest {

    private static final String TITLE = "Inception";
    private static final String DESCRIPTION = "Dreams";
    private static final String GENRE = "Sci-Fi";
    private static final int DURATION = 148;
    private static final LocalDate RELEASE_DATE = LocalDate.of(2010, 7, 16);
    private static final AgeRating RATING = AgeRating.AGE_12;

    private CreateMovieRequest createRequest() {
        return CreateMovieRequest.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .genre(GENRE)
                .durationMinutes(DURATION)
                .releaseDate(RELEASE_DATE)
                .ageRating(RATING)
                .build();
    }

    private Movie createMovie() {
        return Movie.builder()
                .id(1L)
                .title(TITLE)
                .description(DESCRIPTION)
                .genre(GENRE)
                .durationMinutes(DURATION)
                .releaseDate(RELEASE_DATE)
                .ageRating(RATING)
                .build();
    }

    // ---------------------------------------------------------
    // toEntity
    // ---------------------------------------------------------

    @Test
    @DisplayName("should map CreateMovieRequest to Movie entity correctly")
    void shouldMapToEntity() {
        // given
        CreateMovieRequest request = createRequest();

        // when
        Movie result = MovieMapper.toEntity(request);

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getUuid()).isNotNull();
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(result.getGenre()).isEqualTo(GENRE);
        assertThat(result.getDurationMinutes()).isEqualTo(DURATION);
        assertThat(result.getReleaseDate()).isEqualTo(RELEASE_DATE);
        assertThat(result.getAgeRating()).isEqualTo(RATING);
    }

    // ---------------------------------------------------------
    // toResponse
    // ---------------------------------------------------------

    @Test
    @DisplayName("should map Movie entity to MovieResponse correctly")
    void shouldMapToResponse() {
        // given
        Movie movie = createMovie();

        // when
        MovieResponse response = MovieMapper.toResponse(movie);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(TITLE);
        assertThat(response.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getGenre()).isEqualTo(GENRE);
        assertThat(response.getDurationMinutes()).isEqualTo(DURATION);
        assertThat(response.getReleaseDate()).isEqualTo(RELEASE_DATE);
        assertThat(response.getAgeRating()).isEqualTo(RATING);
    }

}
