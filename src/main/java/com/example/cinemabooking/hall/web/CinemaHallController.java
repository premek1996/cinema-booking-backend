package com.example.cinemabooking.hall.web;

import com.example.cinemabooking.hall.dto.CinemaHallResponse;
import com.example.cinemabooking.hall.dto.CreateCinemaHallRequest;
import com.example.cinemabooking.hall.service.CinemaHallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
@RequiredArgsConstructor
public class CinemaHallController {

    private final CinemaHallService cinemaHallService;

    @GetMapping
    List<CinemaHallResponse> getAllCinemaHalls() {
        return cinemaHallService.getAllCinemaHalls();
    }

    @GetMapping("/{id}")
    CinemaHallResponse getCinemaHallById(@PathVariable Long id) {
        return cinemaHallService.getCinemaHallById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CinemaHallResponse createCinemaHall(@RequestBody @Valid CreateCinemaHallRequest createCinemaHallRequest) {
        return cinemaHallService.createCinemaHall(createCinemaHallRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCinemaHall(@PathVariable Long id) {
        cinemaHallService.deleteCinemaHall(id);
    }

}
