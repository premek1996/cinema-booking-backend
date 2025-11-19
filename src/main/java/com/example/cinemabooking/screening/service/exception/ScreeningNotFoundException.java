package com.example.cinemabooking.screening.service.exception;

public class ScreeningNotFoundException extends RuntimeException {
    public ScreeningNotFoundException(Long id) {
        super("Screening with id " + id + " not found.");
    }
}
