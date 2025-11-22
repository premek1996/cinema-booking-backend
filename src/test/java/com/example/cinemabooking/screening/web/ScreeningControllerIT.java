package com.example.cinemabooking.screening.web;

import com.example.cinemabooking.BaseIT;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.repository.CinemaHallRepository;
import com.example.cinemabooking.movie.entity.AgeRating;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.repository.MovieRepository;
import com.example.cinemabooking.screening.entity.Screening;
import com.example.cinemabooking.screening.repository.ScreeningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class ScreeningControllerIT extends BaseIT {

    private static final String BASE_URL = "/api/screening";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaHallRepository hallRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    private Movie movie;
    private CinemaHall hall;
    private Screening screening;

    @BeforeEach
    void setUp() {
        movie = movieRepository.save(
                Movie.builder()
                        .title("Inception")
                        .description("Dreams")
                        .genre("Sci-Fi")
                        .durationMinutes(148)
                        .releaseDate(LocalDate.of(2010, 7, 16))
                        .ageRating(AgeRating.AGE_12)
                        .build()
        );

        hall = hallRepository.save(
                CinemaHall.builder()
                        .name("Sala 1")
                        .rows(10)
                        .seatsPerRow(20)
                        .build()
        );

        screening = screeningRepository.save(
                Screening.builder()
                        .movie(movie)
                        .cinemaHall(hall)
                        .startTime(LocalDateTime.of(2040, 1, 1, 14, 0))
                        .endTime(LocalDateTime.of(2040, 1, 1, 16, 28))
                        .price(BigDecimal.valueOf(25))
                        .build()
        );
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Test
    @DisplayName("GET /api/screening should return list of screenings")
    void shouldReturnAllScreenings() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(screening.getId()));
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Test
    @DisplayName("GET /api/screening/{id} should return screening")
    void shouldReturnScreeningById() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + screening.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieTitle").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/screening/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // GET BY MOVIE
    // ============================================================

    @Test
    @DisplayName("GET /movie/{movieId} should return screenings for movie")
    void shouldReturnByMovie() throws Exception {
        mockMvc.perform(get(BASE_URL + "/movie/" + movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].movieId").value(movie.getId()));
    }

    // ============================================================
    // GET BY HALL
    // ============================================================

    @Test
    @DisplayName("GET /hall/{hallId} should return screenings for hall")
    void shouldReturnByHall() throws Exception {
        mockMvc.perform(get(BASE_URL + "/hall/" + hall.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cinemaHallId").value(hall.getId()));
    }

    // ============================================================
    // GET BY DATE
    // ============================================================

    @Test
    @DisplayName("GET /date/{date} should return screenings on given date")
    void shouldReturnByDate() throws Exception {
        mockMvc.perform(get(BASE_URL + "/date/2040-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(screening.getId()));
    }

    // ============================================================
    // POST CREATE
    // ============================================================

    @Test
    @DisplayName("POST should create screening when valid")
    void shouldCreateScreening() throws Exception {
        String json = """
                {
                  "movieId": %d,
                  "cinemaHallId": %d,
                  "startTime": "2026-01-10T18:00:00",
                  "price": 30.00
                }
                """.formatted(movie.getId(), hall.getId());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movieTitle").value("Inception"));

        assertThat(screeningRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("POST should return 400 for invalid JSON")
    void shouldReturn400ForInvalidJson() throws Exception {
        String invalidJson = """
                {
                  "movieId": null,
                  "cinemaHallId": null,
                  "startTime": null,
                  "price": -10
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").exists());
    }

    @Test
    @DisplayName("POST should return 409 when time conflict occurs")
    void shouldReturn409OnConflict() throws Exception {
        // screening from setup:
        // 2040-01-01 14:00 → 16:28
        String conflictJson = """
                {
                  "movieId": %d,
                  "cinemaHallId": %d,
                  "startTime": "2040-01-01T15:00:00",
                  "price": 25.00
                }
                """.formatted(movie.getId(), hall.getId());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Test
    @DisplayName("DELETE should remove screening when exists")
    void shouldDeleteScreening() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + screening.getId()))
                .andExpect(status().isNoContent());

        assertThat(screeningRepository.findById(screening.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE should return 404 when not exists")
    void shouldReturn404WhenDeleteNonExisting() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/99999"))
                .andExpect(status().isNotFound());
    }

}
