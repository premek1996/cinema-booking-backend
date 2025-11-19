package com.example.cinemabooking.hall.service.exception;

public class CinemaHallAlreadyExistsException extends RuntimeException {
    public CinemaHallAlreadyExistsException(String name) {
        super("Cinema hall with name '" + name + "' already exists.");
    }
}
