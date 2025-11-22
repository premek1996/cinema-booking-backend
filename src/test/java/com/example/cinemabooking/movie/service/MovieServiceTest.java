package com.example.cinemabooking.movie.service;

import com.example.cinemabooking.movie.dto.CreateMovieRequest;
import com.example.cinemabooking.movie.dto.MovieResponse;
import com.example.cinemabooking.movie.dto.UpdateMovieRequest;
import com.example.cinemabooking.movie.entity.AgeRating;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.repository.MovieRepository;
import com.example.cinemabooking.movie.service.exception.MovieAlreadyExistsException;
import com.example.cinemabooking.movie.service.exception.MovieNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    private static final long EXISTING_ID = 1L;
    private static final long NON_EXISTING_ID = 999L;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(EXISTING_ID)
                .title("Inception")
                .description("Dreams")
                .genre("Sci-Fi")
                .durationMinutes(148)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .ageRating(AgeRating.AGE_12)
                .build();
    }

    private CreateMovieRequest createMovieRequest() {
        return CreateMovieRequest.builder()
                .title("Inception")
                .description("Dreams")
                .genre("Sci-Fi")
                .durationMinutes(148)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .ageRating(AgeRating.AGE_12)
                .build();
    }

    private UpdateMovieRequest updateMovieRequest() {
        return UpdateMovieRequest.builder()
                .title(Optional.of("The Dark Knight"))
                .description(Optional.of("Batman fights the Joker in Gotham City."))
                .genre(Optional.of("Action"))
                .durationMinutes(Optional.of(152))
                .releaseDate(Optional.of(LocalDate.of(2008, 7, 16)))
                .build();
    }

    // ----------------------------------------
    // GET ALL
    // ----------------------------------------

    @Test
    @DisplayName("should return list of movies when getAllMovies() is called")
    void shouldReturnAllMovies() {
        // given
        given(movieRepository.findAll()).willReturn(List.of(movie));

        // when
        List<MovieResponse> result = movieService.getAllMovies();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Inception");

        verify(movieRepository).findAll();
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("should return empty list when no movies exist")
    void shouldReturnEmptyListWhenNoMoviesExist() {
        // given
        given(movieRepository.findAll()).willReturn(List.of());

        // when
        List<MovieResponse> result = movieService.getAllMovies();

        // then
        assertThat(result).isEmpty();

        verify(movieRepository).findAll();
        verifyNoMoreInteractions(movieRepository);
    }

    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------

    @Test
    @DisplayName("should return movie when found by id")
    void shouldReturnMovieById() {
        // given
        given(movieRepository.findById(EXISTING_ID)).willReturn(Optional.of(movie));

        // when
        MovieResponse result = movieService.getMovieById(EXISTING_ID);

        // then
        assertThat(result.getTitle()).isEqualTo("Inception");

        verify(movieRepository).findById(EXISTING_ID);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("should throw MovieNotFoundException when movie does not exist")
    void shouldThrowExceptionWhenMovieNotFound() {
        // given
        given(movieRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> movieService.getMovieById(NON_EXISTING_ID))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(movieRepository);
    }

    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    @Test
    @DisplayName("should save movie when title not exists")
    void shouldSaveNewMovie() {
        // given
        CreateMovieRequest request = createMovieRequest();
        given(movieRepository.findByTitle(request.getTitle())).willReturn(Optional.empty());
        given(movieRepository.save(any(Movie.class))).willReturn(movie);

        // when
        MovieResponse result = movieService.createMovie(request);

        // then
        assertThat(result.getTitle()).isEqualTo("Inception");

        verify(movieRepository).findByTitle(request.getTitle());
        verify(movieRepository).save(any(Movie.class));
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("should throw exception when movie already exists")
    void shouldThrowExceptionWhenMovieAlreadyExists() {
        // given
        CreateMovieRequest request = createMovieRequest();
        given(movieRepository.findByTitle(request.getTitle())).willReturn(Optional.of(movie));

        // when + then
        assertThatThrownBy(() -> movieService.createMovie(request))
                .isInstanceOf(MovieAlreadyExistsException.class);

        verify(movieRepository).findByTitle(request.getTitle());
        verify(movieRepository, never()).save(any(Movie.class));
        verifyNoMoreInteractions(movieRepository);
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------
    @Test
    @DisplayName("should throw MovieNotFoundException when updating nonexistent movie")
    void shouldThrowWhenUpdatingNonexistentMovie() {
        // given
        given(movieRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> movieService.updateMovie(NON_EXISTING_ID, updateMovieRequest()))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("should update movie")
    void shouldUpdateMovie() {
        // given
        UpdateMovieRequest updateMovieRequest = updateMovieRequest();
        given(movieRepository.findById(EXISTING_ID)).willReturn(Optional.of(movie));
        given(movieRepository.findByTitle(updateMovieRequest.getTitle().get())).willReturn(Optional.empty());

        // when + then
        MovieResponse response = movieService.updateMovie(EXISTING_ID, updateMovieRequest);
        assertThat(response.getId()).isEqualTo(EXISTING_ID);
        assertThat(response.getTitle()).isEqualTo(updateMovieRequest.getTitle().get());
        assertThat(response.getGenre()).isEqualTo(updateMovieRequest.getGenre().get());
        assertThat(response.getDurationMinutes()).isEqualTo(updateMovieRequest.getDurationMinutes().get());
        assertThat(response.getReleaseDate()).isEqualTo(updateMovieRequest.getReleaseDate().get());
        assertThat(response.getAgeRating()).isEqualTo(movie.getAgeRating());

        verify(movieRepository).findById(EXISTING_ID);
        verify(movieRepository).findByTitle(updateMovieRequest.getTitle().get());
        verifyNoMoreInteractions(movieRepository);
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------

    @Test
    @DisplayName("should delete movie when exists")
    void shouldDeleteMovie() {
        // given
        given(movieRepository.findById(EXISTING_ID)).willReturn(Optional.of(movie));

        // when
        movieService.deleteMovie(EXISTING_ID);

        // then
        verify(movieRepository).findById(EXISTING_ID);
        verify(movieRepository).delete(movie);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("should throw MovieNotFoundException when deleting nonexistent movie")
    void shouldThrowWhenDeletingNonexistentMovie() {
        // given
        given(movieRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> movieService.deleteMovie(NON_EXISTING_ID))
                .isInstanceOf(MovieNotFoundException.class);

        verify(movieRepository).findById(NON_EXISTING_ID);
        verify(movieRepository, never()).delete(any());
        verifyNoMoreInteractions(movieRepository);
    }

}
