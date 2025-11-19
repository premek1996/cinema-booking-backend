package com.example.cinemabooking.screening.web;

import com.example.cinemabooking.screening.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/screening")
public class ScreeningController {

    private final ScreeningService screeningService;
}
