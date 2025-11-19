package com.example.cinemabooking.screening.service.exception;

import java.time.LocalDateTime;

public class ScreeningTimeConflictException extends RuntimeException {
    public ScreeningTimeConflictException(String hallName, LocalDateTime startTime, LocalDateTime endTime) {
        super("Cinema hall '" + hallName + "' is already occupied between " + startTime + " and " + endTime + ".");
    }
}
