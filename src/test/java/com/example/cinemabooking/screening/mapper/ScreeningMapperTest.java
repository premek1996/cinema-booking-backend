package com.example.cinemabooking.screening.mapper;

import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.screening.dto.CreateScreeningRequest;
import com.example.cinemabooking.screening.dto.ScreeningResponse;
import com.example.cinemabooking.screening.entity.Screening;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ScreeningMapperTest {

    private final LocalDateTime START = LocalDateTime.of(2025, 1, 15, 14, 0);
    private final LocalDateTime END = LocalDateTime.of(2025, 1, 15, 16, 30);

    private Movie createMovie() {
        return Movie.builder()
                .id(10L)
                .title("Inception")
                .genre("Sci-Fi")
                .durationMinutes(150)
                .build();
    }

    private CinemaHall createHall() {
        return CinemaHall.builder()
                .id(20L)
                .name("Sala 1")
                .rows(5)
                .seatsPerRow(10)
                .build();
    }

    private CreateScreeningRequest createRequest() {
        return CreateScreeningRequest.builder()
                .movieId(10L)
                .cinemaHallId(20L)
                .startTime(START)
                .price(BigDecimal.valueOf(25.50))
                .build();
    }

    // ---------------------------------------------------------
    // toEntity
    // ---------------------------------------------------------

    @Test
    @DisplayName("should map CreateScreeningRequest + Movie + Hall to Screening entity correctly")
    void shouldMapToEntity() {
        // given
        Movie movie = createMovie();
        CinemaHall hall = createHall();
        CreateScreeningRequest request = createRequest();

        // when
        Screening result = ScreeningMapper.toEntity(request, movie, hall, END);

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getMovie()).isEqualTo(movie);
        assertThat(result.getCinemaHall()).isEqualTo(hall);
        assertThat(result.getStartTime()).isEqualTo(START);
        assertThat(result.getEndTime()).isEqualTo(END);
        assertThat(result.getPrice()).isEqualByComparingTo("25.50");
    }

    // ---------------------------------------------------------
    // toResponse
    // ---------------------------------------------------------

    @Test
    @DisplayName("should map Screening entity to ScreeningResponse correctly")
    void shouldMapToResponse() {
        // given
        Movie movie = createMovie();
        CinemaHall hall = createHall();

        Screening screening = Screening.builder()
                .id(99L)
                .movie(movie)
                .cinemaHall(hall)
                .startTime(START)
                .endTime(END)
                .price(BigDecimal.valueOf(30.00))
                .build();

        // when
        ScreeningResponse response = ScreeningMapper.toResponse(screening);

        // then
        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getMovieId()).isEqualTo(10L);
        assertThat(response.getMovieTitle()).isEqualTo("Inception");
        assertThat(response.getMovieGenre()).isEqualTo("Sci-Fi");
        assertThat(response.getDurationMinutes()).isEqualTo(150);

        assertThat(response.getCinemaHallId()).isEqualTo(20L);
        assertThat(response.getCinemaHallName()).isEqualTo("Sala 1");
        assertThat(response.getHallCapacity()).isEqualTo(5 * 10);

        assertThat(response.getStartTime()).isEqualTo(START);
        assertThat(response.getEndTime()).isEqualTo(END);
        assertThat(response.getPrice()).isEqualByComparingTo("30.00");
    }

}
