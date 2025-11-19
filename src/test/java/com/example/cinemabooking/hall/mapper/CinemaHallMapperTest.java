package com.example.cinemabooking.hall.mapper;

import com.example.cinemabooking.hall.dto.CinemaHallResponse;
import com.example.cinemabooking.hall.dto.CreateCinemaHallRequest;
import com.example.cinemabooking.hall.dto.SeatResponse;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.entity.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CinemaHallMapperTest {

    private static final Long ID = 1L;
    private static final String NAME = "Sala 1";
    private static final int ROWS = 5;
    private static final int SEATS_PER_ROW = 10;

    private CreateCinemaHallRequest createRequest() {
        return CreateCinemaHallRequest.builder()
                .name(NAME)
                .rows(ROWS)
                .seatsPerRow(SEATS_PER_ROW)
                .build();
    }

    private CinemaHall createCinemaHall() {
        CinemaHall cinemaHall = CinemaHall.builder()
                .id(ID)
                .name(NAME)
                .rows(ROWS)
                .seatsPerRow(SEATS_PER_ROW)
                .build();

        Seat s1 = Seat.builder()
                .id(100L)
                .rowNumber(1)
                .seatNumber(1)
                .cinemaHall(cinemaHall)
                .build();

        Seat s2 = Seat.builder()
                .id(101L)
                .rowNumber(1)
                .seatNumber(2)
                .cinemaHall(cinemaHall)
                .build();

        cinemaHall.setSeats(List.of(s1, s2));

        return cinemaHall;
    }

    // ---------------------------------------------------------
    // toEntity
    // ---------------------------------------------------------

    @Test
    @DisplayName("should map CreateCinemaHallRequest to CinemaHall entity correctly")
    void shouldMapToEntity() {
        // given
        CreateCinemaHallRequest request = createRequest();

        // when
        CinemaHall result = CinemaHallMapper.toEntity(request);

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getRows()).isEqualTo(ROWS);
        assertThat(result.getSeatsPerRow()).isEqualTo(SEATS_PER_ROW);
        assertThat(result.getSeats()).isEmpty();
    }

    // ---------------------------------------------------------
    // toResponse
    // ---------------------------------------------------------

    @Test
    @DisplayName("should map CinemaHall entity to CinemaHallResponse with seats correctly")
    void shouldMapToResponse_withSeats() {
        // given
        CinemaHall cinemaHall = createCinemaHall();

        // when
        CinemaHallResponse response = CinemaHallMapper.toResponse(cinemaHall);

        // then
        assertThat(response.getId()).isEqualTo(ID);
        assertThat(response.getName()).isEqualTo(NAME);
        assertThat(response.getRows()).isEqualTo(ROWS);
        assertThat(response.getSeatsPerRow()).isEqualTo(SEATS_PER_ROW);

        assertThat(response.getSeats()).hasSize(2);

        SeatResponse s1 = response.getSeats().get(0);
        SeatResponse s2 = response.getSeats().get(1);

        assertThat(s1.getRowNumber()).isEqualTo(1);
        assertThat(s1.getSeatNumber()).isEqualTo(1);

        assertThat(s2.getRowNumber()).isEqualTo(1);
        assertThat(s2.getSeatNumber()).isEqualTo(2);
    }

}