package com.example.cinemabooking.screening.service;

import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.service.CinemaHallService;
import com.example.cinemabooking.movie.entity.Movie;
import com.example.cinemabooking.movie.service.MovieService;
import com.example.cinemabooking.screening.dto.CreateScreeningRequest;
import com.example.cinemabooking.screening.dto.ScreeningResponse;
import com.example.cinemabooking.screening.entity.Screening;
import com.example.cinemabooking.screening.mapper.ScreeningMapper;
import com.example.cinemabooking.screening.repository.ScreeningRepository;
import com.example.cinemabooking.screening.service.exception.ScreeningNotFoundException;
import com.example.cinemabooking.screening.service.exception.ScreeningTimeConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final CinemaHallService cinemaHallService;

    private static LocalDateTime getScreeningEndTime(LocalDateTime startTime, int durationMinutes) {
        return startTime.plusMinutes(durationMinutes);
    }

    @Transactional(readOnly = true)
    public List<ScreeningResponse> getAllScreenings() {
        return screeningRepository.findAll().stream().map(ScreeningMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ScreeningResponse getScreeningById(Long id) {
        Screening screening = getScreeningOrThrow(id);
        return ScreeningMapper.toResponse(screening);
    }

    private Screening getScreeningOrThrow(Long id) {
        return screeningRepository.findById(id)
                .orElseThrow(() -> new ScreeningNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ScreeningResponse> getScreeningsByMovie(Long movieId) {
        return screeningRepository.findByMovieId(movieId).stream().map(ScreeningMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ScreeningResponse> getScreeningsByCinemaHall(Long cinemaHallId) {
        return screeningRepository.findByCinemaHallId(cinemaHallId).stream().map(ScreeningMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ScreeningResponse> getScreeningsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return screeningRepository.findByStartTimeBetween(startOfDay, endOfDay).stream().map(ScreeningMapper::toResponse).toList();
    }

    @Transactional
    public ScreeningResponse createScreening(CreateScreeningRequest request) {
        Movie movie = movieService.getMovieOrThrow(request.getMovieId());
        CinemaHall cinemaHall = cinemaHallService.getCinemaHallOrThrow(request.getCinemaHallId());
        LocalDateTime endTime = getScreeningEndTime(request.getStartTime(), movie.getDurationMinutes());
        validateNoTimeConflict(cinemaHall, request.getStartTime(), endTime);
        Screening screening = ScreeningMapper.toEntity(request, movie, cinemaHall, endTime);
        return ScreeningMapper.toResponse(screeningRepository.save(screening));
    }

    private void validateNoTimeConflict(CinemaHall cinemaHall, LocalDateTime startTime, LocalDateTime endTime) {
        if (screeningRepository.existsTimeConflict(cinemaHall.getId(), startTime, endTime)) {
            throw new ScreeningTimeConflictException(cinemaHall.getName(), startTime, endTime);
        }
    }

    @Transactional
    public void deleteScreening(Long id) {
        Screening screening = getScreeningOrThrow(id);
        screeningRepository.delete(screening);
    }


}
