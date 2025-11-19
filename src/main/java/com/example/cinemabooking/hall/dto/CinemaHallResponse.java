package com.example.cinemabooking.hall.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CinemaHallResponse {

    Long id;
    String name;
    int rows;
    int seatsPerRow;
    List<SeatResponse> seats;

}
