package com.example.cinemabooking.movie.service.exception;

public class MovieAlreadyExistsException extends RuntimeException {
    public MovieAlreadyExistsException(String title) {
        super("Movie with title '" + title + "' already exists.");
    }
}

