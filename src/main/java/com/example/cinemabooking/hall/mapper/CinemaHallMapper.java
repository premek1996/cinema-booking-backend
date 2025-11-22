package com.example.cinemabooking.hall.mapper;

import com.example.cinemabooking.hall.dto.CinemaHallResponse;
import com.example.cinemabooking.hall.dto.CreateCinemaHallRequest;
import com.example.cinemabooking.hall.dto.SeatResponse;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.entity.Seat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CinemaHallMapper {

    public static CinemaHall toEntity(CreateCinemaHallRequest request) {
        return CinemaHall.builder()
                .name(request.getName())
                .rows(request.getRows())
                .seatsPerRow(request.getSeatsPerRow())
                .build();
    }

    public static CinemaHallResponse toResponse(CinemaHall cinemaHall) {
        return CinemaHallResponse.builder()
                .id(cinemaHall.getId())
                .name(cinemaHall.getName())
                .rows(cinemaHall.getRows())
                .seatsPerRow(cinemaHall.getSeatsPerRow())
                .seats(mapSeats(cinemaHall.getSeats()))
                .build();
    }

    private static List<SeatResponse> mapSeats(Set<Seat> seats) {
        return seats.stream()
                .map(CinemaHallMapper::toSeatResponse)
                .collect(Collectors.toList());
    }

    private static SeatResponse toSeatResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .build();
    }

}
