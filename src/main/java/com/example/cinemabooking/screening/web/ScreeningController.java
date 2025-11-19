package com.example.cinemabooking.screening.web;

import com.example.cinemabooking.screening.dto.CreateScreeningRequest;
import com.example.cinemabooking.screening.dto.ScreeningResponse;
import com.example.cinemabooking.screening.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/screening")
public class ScreeningController {

    private final ScreeningService screeningService;

    @GetMapping
    public List<ScreeningResponse> getAllScreenings() {
        return screeningService.getAllScreenings();
    }

    @GetMapping("/{id}")
    public ScreeningResponse getScreeningById(@PathVariable Long id) {
        return screeningService.getScreeningById(id);
    }

    @GetMapping("/movie/{movieId}")
    public List<ScreeningResponse> getScreeningsByMovie(@PathVariable Long movieId) {
        return screeningService.getScreeningsByMovie(movieId);
    }

    @GetMapping("/hall/{cinemaHallId}")
    public List<ScreeningResponse> getScreeningsByCinemaHall(@PathVariable Long cinemaHallId) {
        return screeningService.getScreeningsByCinemaHall(cinemaHallId);
    }

    @GetMapping("/date/{date}")
    public List<ScreeningResponse> getScreeningsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return screeningService.getScreeningsByDate(date);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScreeningResponse createScreening(@Valid @RequestBody CreateScreeningRequest request) {
        return screeningService.createScreening(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteScreening(@PathVariable Long id) {
        screeningService.deleteScreening(id);
    }

}
