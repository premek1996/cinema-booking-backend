package com.example.cinemabooking.hall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCinemaHallRequest {

    @NotBlank
    private String name;

    @Min(1)
    private int rows;

    @Min(1)
    private int seatsPerRow;

}
