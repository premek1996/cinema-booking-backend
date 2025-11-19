package com.example.cinemabooking.screening.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class ScreeningResponse {

    Long id;
    Long movieId;
    String movieTitle;
    String movieGenre;
    int durationMinutes;
    Long cinemaHallId;
    String cinemaHallName;
    int hallCapacity;
    LocalDateTime startTime;
    LocalDateTime endTime;
    BigDecimal price;

}
