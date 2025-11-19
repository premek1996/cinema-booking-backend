package com.example.cinemabooking.hall.service;

import com.example.cinemabooking.hall.dto.CinemaHallResponse;
import com.example.cinemabooking.hall.dto.CreateCinemaHallRequest;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.entity.Seat;
import com.example.cinemabooking.hall.repository.CinemaHallRepository;
import com.example.cinemabooking.hall.service.exception.CinemaHallAlreadyExistsException;
import com.example.cinemabooking.hall.service.exception.CinemaHallNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaHallServiceTest {

    private static final long EXISTING_ID = 1L;
    private static final long NON_EXISTING_ID = 999L;

    @Mock
    private CinemaHallRepository cinemaHallRepository;

    @InjectMocks
    private CinemaHallService cinemaHallService;

    private CinemaHall hall;

    @BeforeEach
    void setUp() {
        hall = CinemaHall.builder()
                .id(EXISTING_ID)
                .name("Sala 1")
                .rows(2)
                .seatsPerRow(3)
                .build();

        Seat s1 = Seat.builder().id(10L).rowNumber(1).seatNumber(1).cinemaHall(hall).build();
        Seat s2 = Seat.builder().id(11L).rowNumber(1).seatNumber(2).cinemaHall(hall).build();
        Seat s3 = Seat.builder().id(12L).rowNumber(1).seatNumber(3).cinemaHall(hall).build();
        Seat s4 = Seat.builder().id(13L).rowNumber(2).seatNumber(1).cinemaHall(hall).build();
        Seat s5 = Seat.builder().id(14L).rowNumber(2).seatNumber(2).cinemaHall(hall).build();
        Seat s6 = Seat.builder().id(15L).rowNumber(2).seatNumber(3).cinemaHall(hall).build();

        hall.setSeats(List.of(s1, s2, s3, s4, s5, s6));
    }

    private CreateCinemaHallRequest sampleRequest() {
        return CreateCinemaHallRequest.builder()
                .name("Sala 1")
                .rows(2)
                .seatsPerRow(3)
                .build();
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Test
    @DisplayName("should return list of halls")
    void shouldReturnAllHalls() {

        // given
        given(cinemaHallRepository.findAll()).willReturn(List.of(hall));

        // when
        List<CinemaHallResponse> result = cinemaHallService.getAllCinemaHalls();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Sala 1");
        assertThat(result.getFirst().getSeats()).hasSize(6);

        verify(cinemaHallRepository).findAll();
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    @Test
    @DisplayName("should return empty list when no halls exist")
    void shouldReturnEmptyListWhenNoHallsExist() {

        // given
        given(cinemaHallRepository.findAll()).willReturn(List.of());

        // when
        List<CinemaHallResponse> result = cinemaHallService.getAllCinemaHalls();

        // then
        assertThat(result).isEmpty();

        verify(cinemaHallRepository).findAll();
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Test
    @DisplayName("should return hall by id")
    void shouldReturnHallById() {

        // given
        given(cinemaHallRepository.findById(EXISTING_ID)).willReturn(Optional.of(hall));

        // when
        CinemaHallResponse result = cinemaHallService.getCinemaHallById(EXISTING_ID);

        // then
        assertThat(result.getName()).isEqualTo("Sala 1");
        assertThat(result.getSeats()).hasSize(6);

        verify(cinemaHallRepository).findById(EXISTING_ID);
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    @Test
    @DisplayName("should throw exception when hall not found")
    void shouldThrowWhenHallNotFound() {

        // given
        given(cinemaHallRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> cinemaHallService.getCinemaHallById(NON_EXISTING_ID))
                .isInstanceOf(CinemaHallNotFoundException.class);

        verify(cinemaHallRepository).findById(NON_EXISTING_ID);
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    // ============================================================
    // CREATE
    // ============================================================

    @Test
    @DisplayName("should create new hall when name is unique")
    void shouldCreateNewHall() {

        // given
        CreateCinemaHallRequest request = sampleRequest();

        given(cinemaHallRepository.findByName(request.getName())).willReturn(Optional.empty());
        given(cinemaHallRepository.save(any(CinemaHall.class))).willReturn(hall);

        // when
        CinemaHallResponse result = cinemaHallService.createCinemaHall(request);

        // then
        assertThat(result.getName()).isEqualTo("Sala 1");
        assertThat(result.getSeats()).hasSize(6);

        verify(cinemaHallRepository).findByName(request.getName());
        verify(cinemaHallRepository).save(any(CinemaHall.class));
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    @Test
    @DisplayName("should throw exception when hall name already exists")
    void shouldThrowWhenNameExists() {

        // given
        CreateCinemaHallRequest request = sampleRequest();
        given(cinemaHallRepository.findByName(request.getName())).willReturn(Optional.of(hall));

        // when + then
        assertThatThrownBy(() -> cinemaHallService.createCinemaHall(request))
                .isInstanceOf(CinemaHallAlreadyExistsException.class);

        verify(cinemaHallRepository).findByName(request.getName());
        verify(cinemaHallRepository, never()).save(any());
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Test
    @DisplayName("should delete hall when exists")
    void shouldDeleteHall() {

        // given
        given(cinemaHallRepository.findById(EXISTING_ID)).willReturn(Optional.of(hall));

        // when
        cinemaHallService.deleteCinemaHall(EXISTING_ID);

        // then
        verify(cinemaHallRepository).findById(EXISTING_ID);
        verify(cinemaHallRepository).delete(hall);
        verifyNoMoreInteractions(cinemaHallRepository);
    }

    @Test
    @DisplayName("should throw exception when deleting nonexistent hall")
    void shouldThrowWhenDeletingNonexistentHall() {

        // given
        given(cinemaHallRepository.findById(NON_EXISTING_ID)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> cinemaHallService.deleteCinemaHall(NON_EXISTING_ID))
                .isInstanceOf(CinemaHallNotFoundException.class);

        verify(cinemaHallRepository).findById(NON_EXISTING_ID);
        verify(cinemaHallRepository, never()).delete(any());
        verifyNoMoreInteractions(cinemaHallRepository);
    }

}
