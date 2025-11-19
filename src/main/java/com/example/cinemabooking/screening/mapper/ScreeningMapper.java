package com.example.cinemabooking.screening.mapper;

import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.screening.dto.CreateScreeningRequest;
import com.example.cinemabooking.screening.dto.ScreeningResponse;
import com.example.cinemabooking.screening.entity.Screening;

import java.time.LocalDateTime;

public class ScreeningMapper {

    public static ScreeningResponse toResponse(Screening screening) {
        Movie movie = screening.getMovie();
        CinemaHall hall = screening.getCinemaHall();
        return ScreeningResponse.builder()
                .id(screening.getId())
                .movieId(movie.getId())
                .movieTitle(movie.getTitle())
                .movieGenre(movie.getGenre())
                .durationMinutes(movie.getDurationMinutes())
                .cinemaHallId(hall.getId())
                .cinemaHallName(hall.getName())
                .hallCapacity(hall.getRows() * hall.getSeatsPerRow())
                .startTime(screening.getStartTime())
                .endTime(screening.getEndTime())
                .price(screening.getPrice())
                .build();
    }

    public static Screening toEntity(CreateScreeningRequest request, Movie movie, CinemaHall cinemaHall, LocalDateTime endTime) {
        return Screening.builder()
                .movie(movie)
                .cinemaHall(cinemaHall)
                .startTime(request.getStartTime())
                .endTime(endTime)
                .price(request.getPrice())
                .build();
    }

}
