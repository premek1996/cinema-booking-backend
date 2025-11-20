package com.example.cinemabooking.movie.repository;

import com.example.cinemabooking.movie.entity.AgeRating;
import com.example.cinemabooking.movie.entity.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class MovieRepositoryTest {

    private static final long NON_EXISTING_ID = 999L;

    @Autowired
    private MovieRepository movieRepository;

    private static Movie createMovie(String title) {
        return Movie.builder()
                .title(title)
                .description("Desc")
                .genre("Drama")
                .durationMinutes(120)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .ageRating(AgeRating.AGE_12)
                .build();
    }

    // -----------------------------------------------------
    // SAVE
    // -----------------------------------------------------

    @Test
    @DisplayName("should save movie and assign ID")
    void shouldSaveMovie() {
        // given
        Movie movie = createMovie("Inception");

        // when
        Movie saved = movieRepository.save(movie);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Inception");
    }

    // -----------------------------------------------------
    // FIND ALL
    // -----------------------------------------------------

    @Test
    @DisplayName("should return all movies")
    void shouldReturnAllMovies() {
        // given
        movieRepository.saveAll(List.of(
                createMovie("A"),
                createMovie("B")
        ));

        // when
        List<Movie> result = movieRepository.findAll();

        // then
        assertThat(result).hasSize(2);
    }

    // -----------------------------------------------------
    // FIND BY ID
    // -----------------------------------------------------

    @Test
    @DisplayName("should return movie when id exists")
    void shouldFindById() {
        // given
        Movie saved = movieRepository.save(createMovie("Avatar"));

        // when
        Optional<Movie> result = movieRepository.findById(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Avatar");
    }

    @Test
    @DisplayName("should return empty Optional when id does not exist")
    void shouldNotFindById() {
        // when
        Optional<Movie> result = movieRepository.findById(NON_EXISTING_ID);

        // then
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------
    // FIND BY TITLE
    // -----------------------------------------------------

    @Test
    @DisplayName("should find movie by title")
    void shouldFindByTitle() {
        // given
        movieRepository.save(createMovie("Matrix"));

        // when
        Optional<Movie> result = movieRepository.findByTitle("Matrix");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Matrix");
    }

    @Test
    @DisplayName("should return empty Optional when title not found")
    void shouldNotFindByTitle() {
        // when
        Optional<Movie> result = movieRepository.findByTitle("Unknown Title");

        // then
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------
    // UNIQUE CONSTRAINT
    // -----------------------------------------------------

    @Test
    @DisplayName("should enforce unique constraint on title")
    void shouldEnforceUniqueTitleConstraint() {
        // given
        movieRepository.save(createMovie("Duplicate"));

        Movie duplicate = createMovie("Duplicate");

        // when + then
        assertThatThrownBy(() -> movieRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // -----------------------------------------------------
    // DELETE
    // -----------------------------------------------------

    @Test
    @DisplayName("should delete movie")
    void shouldDeleteMovie() {
        // given
        Movie saved = movieRepository.save(createMovie("Delete me"));

        // when
        movieRepository.delete(saved);

        // then
        assertThat(movieRepository.existsById(saved.getId())).isFalse();
    }

}
