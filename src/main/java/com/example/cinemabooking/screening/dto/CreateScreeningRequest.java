package com.example.cinemabooking.screening.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScreeningRequest {

    @NotNull
    private Long movieId;

    @NotNull
    private Long cinemaHallId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal price;

}
