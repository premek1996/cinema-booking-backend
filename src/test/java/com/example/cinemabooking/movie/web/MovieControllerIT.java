package com.example.cinemabooking.movie.web;

import com.example.cinemabooking.movie.entity.AgeRating;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MovieControllerIT {

    private static final String VALID_MOVIE_JSON = """
            {
              "title": "Inception",
              "description": "Dreams",
              "genre": "Sci-Fi",
              "durationMinutes": 148,
              "releaseDate": "2010-07-16",
              "ageRating": "AGE_12"
            }
            """;

    private static final String INVALID_MOVIE_JSON = """
            {
              "title": "",
              "description": "",
              "genre": "",
              "durationMinutes": 0,
              "releaseDate": null,
              "ageRating": null
            }
            """;

    private static final long NON_EXISTING_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .title("Inception")
                .description("Dreams")
                .genre("Sci-Fi")
                .durationMinutes(148)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .ageRating(AgeRating.AGE_12)
                .build();
    }

    // ----------------------------------------
    // GET /api/movies
    // ----------------------------------------

    @Test
    @DisplayName("should return empty list when no movies exist")
    void shouldReturnEmptyListWhenNoMoviesExist() throws Exception {
        // given – empty DB

        // when / then
        mockMvc.perform(get("/api/movies"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("[]")
                );
    }

    @Test
    @DisplayName("should return list of movies when movies exist")
    void shouldReturnListOfMovies() throws Exception {
        // given
        Movie saved = movieRepository.save(movie);

        // when / then
        mockMvc.perform(get("/api/movies"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$[0].id").value(saved.getId()),
                        jsonPath("$[0].title").value("Inception"),
                        jsonPath("$[0].genre").value("Sci-Fi")
                );
    }

    // ----------------------------------------
    // GET /api/movies/{id}
    // ----------------------------------------

    @Test
    @DisplayName("should return movie when found by id")
    void shouldReturnMovieById() throws Exception {
        // given
        Movie saved = movieRepository.save(movie);

        // when / then
        mockMvc.perform(get("/api/movies/" + saved.getId()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(saved.getId()),
                        jsonPath("$.title").value("Inception")
                );
    }

    @Test
    @DisplayName("should return 404 when movie not found by id")
    void shouldReturn404WhenMovieNotFound() throws Exception {
        // given – empty DB

        // when / then
        mockMvc.perform(get("/api/movies/" + NON_EXISTING_ID))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.messages[0]").exists(),
                        jsonPath("$.status").value(404),
                        jsonPath("$.timestamp").exists()
                );
    }

    // ----------------------------------------
    // POST /api/movies
    // ----------------------------------------

    @Test
    @DisplayName("should create movie when request is valid")
    void shouldCreateMovieWhenValid() throws Exception {
        // when / then
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MOVIE_JSON))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.title").value("Inception"),
                        jsonPath("$.genre").value("Sci-Fi")
                );

        assertThat(movieRepository.findByTitle("Inception")).isPresent();
    }

    @Test
    @DisplayName("should return 400 when request invalid")
    void shouldReturn400WhenRequestInvalid() throws Exception {
        // when / then
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_MOVIE_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.messages").isArray(),
                        jsonPath("$.status").value(400)
                );
    }

    @Test
    @DisplayName("should return 409 when movie with same title exists")
    void shouldReturn409WhenMovieAlreadyExists() throws Exception {
        // given
        movieRepository.save(movie);

        // when / then
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_MOVIE_JSON))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.messages[0]").exists(),
                        jsonPath("$.status").value(409)
                );

        assertThat(movieRepository.count()).isEqualTo(1);
    }

    // ----------------------------------------
    // DELETE /api/movies/{id}
    // ----------------------------------------

    @Test
    @DisplayName("should delete movie when exists")
    void shouldDeleteMovieWhenExists() throws Exception {
        // given
        Movie saved = movieRepository.save(movie);

        // when / then
        mockMvc.perform(delete("/api/movies/" + saved.getId()))
                .andExpectAll(
                        status().isNoContent()
                );

        assertThat(movieRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("should return 404 when deleting nonexistent movie")
    void shouldReturn404WhenDeletingNonexistentMovie() throws Exception {
        // when / then
        mockMvc.perform(delete("/api/movies/" + NON_EXISTING_ID))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.messages[0]").exists(),
                        jsonPath("$.status").value(404)
                );
    }

}
