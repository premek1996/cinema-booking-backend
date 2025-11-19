package com.example.cinemabooking.hall.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SeatResponse {

    int rowNumber;
    int seatNumber;

}
