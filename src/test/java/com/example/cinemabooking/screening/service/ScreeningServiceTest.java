package com.example.cinemabooking.screening.service;

import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.service.CinemaHallService;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.service.MovieService;
import com.example.cinemabooking.screening.dto.CreateScreeningRequest;
import com.example.cinemabooking.screening.dto.ScreeningResponse;
import com.example.cinemabooking.screening.entity.Screening;
import com.example.cinemabooking.screening.repository.ScreeningRepository;
import com.example.cinemabooking.screening.service.exception.ScreeningNotFoundException;
import com.example.cinemabooking.screening.service.exception.ScreeningTimeConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    private static final long ID = 1L;
    private static final long NON_EXISTING_ID = 999L;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private MovieService movieService;

    @Mock
    private CinemaHallService cinemaHallService;

    @InjectMocks
    private ScreeningService screeningService;

    private Movie movie;
    private CinemaHall hall;
    private Screening screening;
    private CreateScreeningRequest request;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(10L)
                .title("Inception")
                .genre("Sci-Fi")
                .durationMinutes(148)
                .build();

        hall = CinemaHall.builder()
                .id(5L)
                .name("Sala 1")
                .rows(10)
                .seatsPerRow(20)
                .build();

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 14, 0);
        LocalDateTime end = start.plusMinutes(movie.getDurationMinutes());

        screening = Screening.builder()
                .id(ID)
                .movie(movie)
                .cinemaHall(hall)
                .startTime(start)
                .endTime(end)
                .price(BigDecimal.valueOf(25))
                .build();

        request = CreateScreeningRequest.builder()
                .movieId(movie.getId())
                .cinemaHallId(hall.getId())
                .startTime(start)
                .price(BigDecimal.valueOf(25))
                .build();
    }

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------

    @Test
    @DisplayName("should return list of screenings")
    void shouldReturnAllScreenings() {

        given(screeningRepository.findAll()).willReturn(List.of(screening));

        List<ScreeningResponse> result = screeningService.getAllScreenings();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMovieTitle()).isEqualTo("Inception");

        verify(screeningRepository).findAll();
        verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    @DisplayName("should return empty list when no screenings exist")
    void shouldReturnEmptyList() {

        given(screeningRepository.findAll()).willReturn(List.of());

        List<ScreeningResponse> result = screeningService.getAllScreenings();

        assertThat(result).isEmpty();

        verify(screeningRepository).findAll();
        verifyNoMoreInteractions(screeningRepository);
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------

    @Test
    @DisplayName("should return screening by id")
    void shouldReturnScreeningById() {

        given(screeningRepository.findById(ID)).willReturn(Optional.of(screening));

        ScreeningResponse result = screeningService.getScreeningById(ID);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getMovieTitle()).isEqualTo("Inception");

        verify(screeningRepository).findById(ID);
        verifyNoMoreInteractions(screeningRepository);
    }

    @Test
    @DisplayName("should throw exception when screening not found")
    void shouldThrowWhenScreeningNotFound() {

        given(screeningRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.getScreeningById(NON_EXISTING_ID))
                .isInstanceOf(ScreeningNotFoundException.class);

        verify(screeningRepository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(screeningRepository);
    }

    // -------------------------------------------------------
    // FILTERS
    // -------------------------------------------------------

    @Test
    @DisplayName("should return screenings by movie id")
    void shouldReturnByMovieId() {

        given(screeningRepository.findByMovieId(movie.getId())).willReturn(List.of(screening));

        List<ScreeningResponse> result = screeningService.getScreeningsByMovie(movie.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMovieId()).isEqualTo(movie.getId());

        verify(screeningRepository).findByMovieId(movie.getId());
    }

    @Test
    @DisplayName("should return screenings by hall id")
    void shouldReturnByHallId() {

        given(screeningRepository.findByCinemaHallId(hall.getId())).willReturn(List.of(screening));

        List<ScreeningResponse> result = screeningService.getScreeningsByCinemaHall(hall.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCinemaHallId()).isEqualTo(hall.getId());

        verify(screeningRepository).findByCinemaHallId(hall.getId());
    }

    @Test
    @DisplayName("should return screenings by date")
    void shouldReturnByDate() {

        LocalDate date = LocalDate.of(2025, 1, 1);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        given(screeningRepository.findByStartTimeBetween(start, end))
                .willReturn(List.of(screening));

        List<ScreeningResponse> result = screeningService.getScreeningsByDate(date);

        assertThat(result).hasSize(1);

        verify(screeningRepository).findByStartTimeBetween(start, end);
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    @Test
    @DisplayName("should create screening when no time conflict")
    void shouldCreateScreening() {

        // given
        given(movieService.getMovieOrThrow(movie.getId())).willReturn(movie);
        given(cinemaHallService.getCinemaHallOrThrow(hall.getId())).willReturn(hall);
        given(screeningRepository.existsTimeConflict(eq(hall.getId()), any(), any())).willReturn(false);
        given(screeningRepository.save(any(Screening.class))).willReturn(screening);

        // when
        ScreeningResponse result = screeningService.createScreening(request);

        // then
        assertThat(result.getMovieTitle()).isEqualTo("Inception");
        assertThat(result.getCinemaHallName()).isEqualTo("Sala 1");

        verify(movieService).getMovieOrThrow(movie.getId());
        verify(cinemaHallService).getCinemaHallOrThrow(hall.getId());
        verify(screeningRepository).existsTimeConflict(eq(hall.getId()), any(), any());
        verify(screeningRepository).save(any(Screening.class));
    }

    @Test
    @DisplayName("should throw exception when screening time conflicts")
    void shouldThrowWhenTimeConflict() {

        given(movieService.getMovieOrThrow(movie.getId())).willReturn(movie);
        given(cinemaHallService.getCinemaHallOrThrow(hall.getId())).willReturn(hall);
        given(screeningRepository.existsTimeConflict(anyLong(), any(), any())).willReturn(true);

        assertThatThrownBy(() -> screeningService.createScreening(request))
                .isInstanceOf(ScreeningTimeConflictException.class);

        verify(screeningRepository).existsTimeConflict(anyLong(), any(), any());
        verify(screeningRepository, never()).save(any());
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    @Test
    @DisplayName("should delete screening when exists")
    void shouldDeleteScreening() {

        given(screeningRepository.findById(ID)).willReturn(Optional.of(screening));

        screeningService.deleteScreening(ID);

        verify(screeningRepository).findById(ID);
        verify(screeningRepository).delete(screening);
    }

    @Test
    @DisplayName("should throw exception when deleting nonexistent screening")
    void shouldThrowWhenDeletingNonexistent() {

        given(screeningRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.deleteScreening(NON_EXISTING_ID))
                .isInstanceOf(ScreeningNotFoundException.class);

        verify(screeningRepository).findById(NON_EXISTING_ID);
        verify(screeningRepository, never()).delete(any());
    }

}
