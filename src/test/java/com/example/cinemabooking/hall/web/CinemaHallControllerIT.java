package com.example.cinemabooking.hall.web;

import com.example.cinemabooking.BaseIT;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.entity.Seat;
import com.example.cinemabooking.hall.repository.CinemaHallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
class CinemaHallControllerIT extends BaseIT {

    private static final String BASE_URL = "/api/halls";

    private static final String VALID_HALL_JSON = """
            {
              "name": "Sala 1",
              "rows": 2,
              "seatsPerRow": 3
            }
            """;

    private static final String INVALID_HALL_JSON = """
            {
              "name": "",
              "rows": 0,
              "seatsPerRow": 0
            }
            """;

    private static final long NON_EXISTING_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    private CinemaHall hall;

    @BeforeEach
    void setUp() {
        hall = CinemaHall.builder()
                .name("Sala 1")
                .rows(2)
                .seatsPerRow(3)
                .build();

        Seat s1 = Seat.builder().rowNumber(1).seatNumber(1).cinemaHall(hall).build();
        Seat s2 = Seat.builder().rowNumber(1).seatNumber(2).cinemaHall(hall).build();
        Seat s3 = Seat.builder().rowNumber(1).seatNumber(3).cinemaHall(hall).build();
        Seat s4 = Seat.builder().rowNumber(2).seatNumber(1).cinemaHall(hall).build();
        Seat s5 = Seat.builder().rowNumber(2).seatNumber(2).cinemaHall(hall).build();
        Seat s6 = Seat.builder().rowNumber(2).seatNumber(3).cinemaHall(hall).build();

        hall.setSeats(Set.of(s1, s2, s3, s4, s5, s6));
    }

    // ----------------------------------------------------------
    // GET /api/halls
    // ----------------------------------------------------------

    @Test
    @DisplayName("GET /api/halls - should return empty list when no halls exist")
    void shouldReturnEmptyListWhenNoHallsExist() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("[]")
                );
    }

    @Test
    @DisplayName("GET /api/halls - should return list of halls")
    void shouldReturnListOfHalls() throws Exception {
        CinemaHall saved = cinemaHallRepository.save(hall);

        mockMvc.perform(get(BASE_URL))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id").value(saved.getId()),
                        jsonPath("$[0].name").value("Sala 1"),
                        jsonPath("$[0].seats").isArray(),
                        jsonPath("$[0].seats.length()").value(6)
                );
    }

    // ----------------------------------------------------------
    // GET /api/halls/{id}
    // ----------------------------------------------------------

    @Test
    @DisplayName("GET /api/halls/{id} - should return hall when exists")
    void shouldReturnHallById() throws Exception {
        CinemaHall saved = cinemaHallRepository.save(hall);

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(saved.getId()),
                        jsonPath("$.name").value("Sala 1"),
                        jsonPath("$.seats.length()").value(6)
                );
    }

    @Test
    @DisplayName("GET /api/halls/{id} - should return 404 when hall not found")
    void shouldReturn404WhenHallNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + NON_EXISTING_ID))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.messages[0]").exists(),
                        jsonPath("$.status").value(404)
                );
    }

    // ----------------------------------------------------------
    // POST /api/halls
    // ----------------------------------------------------------

    @Test
    @DisplayName("POST /api/halls - should create hall when valid")
    void shouldCreateHallWhenValid() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_HALL_JSON))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").value("Sala 1"),
                        jsonPath("$.rows").value(2),
                        jsonPath("$.seatsPerRow").value(3),
                        jsonPath("$.seats.length()").value(6)
                );

        assertThat(cinemaHallRepository.findByName("Sala 1")).isPresent();
    }

    @Test
    @DisplayName("POST /api/halls - should return 400 when invalid request")
    void shouldReturn400WhenInvalid() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_HALL_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.messages").isArray(),
                        jsonPath("$.status").value(400)
                );
    }

    @Test
    @DisplayName("POST /api/halls - should return 409 when hall with same name exists")
    void shouldReturn409WhenHallAlreadyExists() throws Exception {
        cinemaHallRepository.save(hall);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_HALL_JSON))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.messages[0]").exists(),
                        jsonPath("$.status").value(409)
                );

        assertThat(cinemaHallRepository.count()).isEqualTo(1);
    }

    // ----------------------------------------------------------
    // DELETE /api/halls/{id}
    // ----------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/halls/{id} - should delete hall when exists")
    void shouldDeleteHallWhenExists() throws Exception {
        CinemaHall saved = cinemaHallRepository.save(hall);

        mockMvc.perform(delete(BASE_URL + "/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(cinemaHallRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/halls/{id} - should return 404 when hall not found")
    void shouldReturn404WhenDeletingNonexistentHall() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + NON_EXISTING_ID))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.messages[0]").exists(),
                        jsonPath("$.status").value(404)
                );
    }

}
