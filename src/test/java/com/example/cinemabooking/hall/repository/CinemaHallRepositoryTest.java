package com.example.cinemabooking.hall.repository;

import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.entity.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class CinemaHallRepositoryTest {

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    private CinemaHall createCinemaHallWithSeats(String name, int rows, int seatsPerRow) {
        CinemaHall hall = CinemaHall.builder()
                .name(name)
                .rows(rows)
                .seatsPerRow(seatsPerRow)
                .build();

        for (int r = 1; r <= rows; r++) {
            for (int s = 1; s <= seatsPerRow; s++) {
                Seat seat = Seat.builder()
                        .rowNumber(r)
                        .seatNumber(s)
                        .cinemaHall(hall)
                        .build();
                hall.getSeats().add(seat);
            }
        }
        return hall;
    }

    // ---------------------------------------------------------
    // SAVE
    // ---------------------------------------------------------

    @Test
    @DisplayName("should save hall with seats")
    void shouldSaveHallWithSeats() {
        // given
        CinemaHall hall = createCinemaHallWithSeats("Sala Test", 2, 3);

        // when
        CinemaHall saved = cinemaHallRepository.save(hall);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSeats()).hasSize(6);
    }

    // ---------------------------------------------------------
    // FIND ALL with EntityGraph
    // ---------------------------------------------------------

    @Test
    @DisplayName("should load seats using EntityGraph in findAll()")
    void shouldLoadSeatsInFindAll() {
        // given
        cinemaHallRepository.save(createCinemaHallWithSeats("Hall A", 2, 2));

        // when
        List<CinemaHall> result = cinemaHallRepository.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getSeats()).hasSize(4); // entity graph loads seats
    }

    // ---------------------------------------------------------
    // FIND BY ID with EntityGraph
    // ---------------------------------------------------------

    @Test
    @DisplayName("should load hall with seats using EntityGraph in findById()")
    void shouldLoadSeatsInFindById() {
        // given
        CinemaHall saved = cinemaHallRepository.save(createCinemaHallWithSeats("Hall X", 1, 3));

        // when
        Optional<CinemaHall> result = cinemaHallRepository.findById(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getSeats()).hasSize(3);
    }

    // ---------------------------------------------------------
    // FIND BY NAME
    // ---------------------------------------------------------

    @Test
    @DisplayName("should find hall by name")
    void shouldFindByName() {
        // given
        cinemaHallRepository.save(createCinemaHallWithSeats("VIP", 1, 1));

        // when
        Optional<CinemaHall> result = cinemaHallRepository.findByName("VIP");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("VIP");
    }

    @Test
    @DisplayName("should return empty Optional when name not found")
    void shouldNotFindByName() {
        // given
        cinemaHallRepository.save(createCinemaHallWithSeats("VIP", 1, 1));

        // when
        Optional<CinemaHall> result = cinemaHallRepository.findByName("Unknown");

        // then
        assertThat(result).isEmpty();
    }

    // ---------------------------------------------------------
    // UNIQUE name constraint
    // ---------------------------------------------------------

    @Test
    @DisplayName("should enforce unique constraint on hall name")
    void shouldEnforceUniqueName() {
        // given
        cinemaHallRepository.save(createCinemaHallWithSeats("Sala 1", 1, 1));

        // when + then
        assertThatThrownBy(() -> {
            cinemaHallRepository.saveAndFlush(createCinemaHallWithSeats("Sala 1", 2, 2));
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    // ---------------------------------------------------------
    // DELETE (cascade seats)
    // ---------------------------------------------------------

    @Test
    @DisplayName("should delete hall and cascade delete seats")
    void shouldDeleteHallWithSeats() {
        // given
        CinemaHall hall = cinemaHallRepository.save(createCinemaHallWithSeats("To Delete", 2, 2));
        Long hallId = hall.getId();

        // when
        cinemaHallRepository.delete(hall);

        // then
        assertThat(cinemaHallRepository.findById(hallId)).isEmpty();
    }

}
