package com.example.cinemabooking.screening.repository;

import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.repository.CinemaHallRepository;
import com.example.cinemabooking.movie.entity.AgeRating;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.repository.MovieRepository;
import com.example.cinemabooking.screening.entity.Screening;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class ScreeningRepositoryIT {

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaHallRepository hallRepository;

    private Movie movie;
    private CinemaHall hall;

    @BeforeEach
    void setUp() {
        movie = movieRepository.save(
                Movie.builder()
                        .title("Inception")
                        .description("Desc")
                        .genre("Sci-Fi")
                        .durationMinutes(148)
                        .releaseDate(LocalDate.of(2010, 1, 1))
                        .ageRating(AgeRating.AGE_12)
                        .build()
        );

        hall = hallRepository.save(
                CinemaHall.builder()
                        .name("Sala 1")
                        .rows(5)
                        .seatsPerRow(10)
                        .build()
        );
    }

    private Screening createScreening(LocalDateTime start, LocalDateTime end) {
        return Screening.builder()
                .movie(movie)
                .cinemaHall(hall)
                .startTime(start)
                .endTime(end)
                .price(BigDecimal.valueOf(25.0))
                .build();
    }

    // --------------------------------------------
    // SAVE
    // --------------------------------------------
    @Test
    @DisplayName("should save screening and assign ID")
    void shouldSaveScreening() {
        Screening s = createScreening(
                LocalDateTime.of(2025, 1, 1, 12, 0),
                LocalDateTime.of(2025, 1, 1, 14, 28)
        );

        Screening saved = screeningRepository.save(s);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMovie().getId()).isEqualTo(movie.getId());
        assertThat(saved.getCinemaHall().getId()).isEqualTo(hall.getId());
    }

    // --------------------------------------------
    // FIND ALL (EntityGraph)
    // --------------------------------------------
    @Test
    @DisplayName("should return all screenings with movie and hall eagerly loaded")
    void shouldReturnAllScreenings() {
        screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 30)
        ));

        List<Screening> result = screeningRepository.findAll();

        assertThat(result).hasSize(1);

        assertThat(result.getFirst().getMovie().getTitle()).isEqualTo("Inception");
        assertThat(result.getFirst().getCinemaHall().getName()).isEqualTo("Sala 1");
    }

    // --------------------------------------------
    // FIND BY ID
    // --------------------------------------------
    @Test
    @DisplayName("should find screening by id")
    void shouldFindById() {
        Screening saved = screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 30)
        ));

        var result = screeningRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getMovie().getTitle()).isEqualTo("Inception");
    }

    // --------------------------------------------
    // FIND BY MOVIE ID
    // --------------------------------------------
    @Test
    @DisplayName("should return screenings by movie id")
    void shouldFindByMovieId() {
        screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 30)
        ));

        List<Screening> result = screeningRepository.findByMovieId(movie.getId());

        assertThat(result).hasSize(1);
    }

    // --------------------------------------------
    // FIND BY CINEMA HALL ID
    // --------------------------------------------
    @Test
    @DisplayName("should return screenings by cinema hall id")
    void shouldFindByHallId() {
        screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 30)
        ));

        List<Screening> result = screeningRepository.findByCinemaHallId(hall.getId());

        assertThat(result).hasSize(1);
    }

    // --------------------------------------------
    // FIND BY DATE RANGE
    // --------------------------------------------
    @Test
    @DisplayName("should return screenings between start time boundaries")
    void shouldFindByStartTimeBetween() {
        Screening s = createScreening(
                LocalDateTime.of(2025, 1, 1, 15, 0),
                LocalDateTime.of(2025, 1, 1, 17, 30)
        );
        screeningRepository.save(s);

        List<Screening> result = screeningRepository.findByStartTimeBetween(
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 1, 23, 59)
        );

        assertThat(result).hasSize(1);
    }

    // --------------------------------------------
    // EXISTS TIME CONFLICT
    // --------------------------------------------
    @Test
    @DisplayName("should detect time conflict between screenings")
    void shouldDetectTimeConflict() {
        // screening 10:00–12:00
        screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0)
        ));

        boolean conflict = screeningRepository.existsTimeConflict(
                hall.getId(),
                LocalDateTime.of(2025, 1, 1, 11, 0),
                LocalDateTime.of(2025, 1, 1, 13, 0)
        );

        assertThat(conflict).isTrue();
    }

    @Test
    @DisplayName("should NOT detect conflict when times do not overlap")
    void shouldNotDetectConflict() {
        screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0)
        ));

        boolean conflict = screeningRepository.existsTimeConflict(
                hall.getId(),
                LocalDateTime.of(2025, 1, 1, 12, 0),
                LocalDateTime.of(2025, 1, 1, 14, 0)
        );

        assertThat(conflict).isFalse();
    }

    // --------------------------------------------
    // UNIQUE CONSTRAINT (hall_id, start_time)
    // --------------------------------------------
    @Test
    @DisplayName("should enforce unique constraint (hall_id, start_time)")
    void shouldEnforceHallStartTimeUniqueConstraint() {

        screeningRepository.saveAndFlush(createScreening(
                LocalDateTime.of(2025, 1, 1, 15, 0),
                LocalDateTime.of(2025, 1, 1, 17, 0)
        ));

        Screening duplicate = createScreening(
                LocalDateTime.of(2025, 1, 1, 15, 0),
                LocalDateTime.of(2025, 1, 1, 17, 0)
        );

        assertThatThrownBy(() -> screeningRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // --------------------------------------------
    // DELETE
    // --------------------------------------------
    @Test
    @DisplayName("should delete screening")
    void shouldDeleteScreening() {
        Screening saved = screeningRepository.save(createScreening(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0)
        ));

        screeningRepository.delete(saved);

        assertThat(screeningRepository.existsById(saved.getId())).isFalse();
    }

}
