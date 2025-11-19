package com.example.cinemabooking.hall.service.exception;

public class CinemaHallNotFoundException extends RuntimeException {
    public CinemaHallNotFoundException(Long id) {
        super("Cinema hall with id " + id + " not found.");
    }
}
