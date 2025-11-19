package com.example.cinemabooking.common.exception;

import com.example.cinemabooking.hall.service.exception.CinemaHallAlreadyExistsException;
import com.example.cinemabooking.hall.service.exception.CinemaHallNotFoundException;
import com.example.cinemabooking.movie.service.exception.MovieAlreadyExistsException;
import com.example.cinemabooking.movie.service.exception.MovieNotFoundException;
import com.example.cinemabooking.screening.service.exception.ScreeningNotFoundException;
import com.example.cinemabooking.screening.service.exception.ScreeningTimeConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    private static final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_STATUS_MAP = Map.ofEntries(
            Map.entry(MovieNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(MovieAlreadyExistsException.class, HttpStatus.CONFLICT),
            Map.entry(CinemaHallNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(CinemaHallAlreadyExistsException.class, HttpStatus.CONFLICT),
            Map.entry(ScreeningNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(ScreeningTimeConflictException.class, HttpStatus.CONFLICT)
    );

    @ExceptionHandler({
            MovieNotFoundException.class,
            MovieAlreadyExistsException.class,
            CinemaHallNotFoundException.class,
            CinemaHallAlreadyExistsException.class,
            ScreeningNotFoundException.class,
            ScreeningTimeConflictException.class
    })
    ResponseEntity<ApiExceptionResponse> handleKnownExceptions(RuntimeException e) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(e.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        ApiExceptionResponse response = ApiExceptionResponse.builder()
                .messages(List.of(e.getMessage()))
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> messages = e.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ApiExceptionResponse.builder()
                .messages(messages)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiExceptionResponse handleOtherExceptions(Exception e) {
        return ApiExceptionResponse.builder()
                .messages(List.of("Unexpected error occurred."))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

}
