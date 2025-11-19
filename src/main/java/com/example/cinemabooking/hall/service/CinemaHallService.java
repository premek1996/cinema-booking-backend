package com.example.cinemabooking.hall.service;

import com.example.cinemabooking.hall.dto.CinemaHallResponse;
import com.example.cinemabooking.hall.dto.CreateCinemaHallRequest;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.hall.entity.Seat;
import com.example.cinemabooking.hall.mapper.CinemaHallMapper;
import com.example.cinemabooking.hall.repository.CinemaHallRepository;
import com.example.cinemabooking.hall.service.exception.CinemaHallAlreadyExistsException;
import com.example.cinemabooking.hall.service.exception.CinemaHallNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaHallService {

    private final CinemaHallRepository cinemaHallRepository;

    @Transactional(readOnly = true)
    public List<CinemaHallResponse> getAllCinemaHalls() {
        return cinemaHallRepository.findAll()
                .stream()
                .map(CinemaHallMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CinemaHallResponse getCinemaHallById(Long id) {
        CinemaHall cinemaHall = getCinemaHallOrThrow(id);
        return CinemaHallMapper.toResponse(cinemaHall);
    }

    @Transactional(readOnly = true)
    public CinemaHall getCinemaHallOrThrow(Long id) {
        return cinemaHallRepository.findById(id)
                .orElseThrow(() -> new CinemaHallNotFoundException(id));
    }

    @Transactional
    public CinemaHallResponse createCinemaHall(CreateCinemaHallRequest createCinemaHallRequest) {
        String cinemaHallName = createCinemaHallRequest.getName();
        if (cinemaHallRepository.findByName(cinemaHallName).isPresent()) {
            throw new CinemaHallAlreadyExistsException(cinemaHallName);
        }
        CinemaHall cinemaHall = CinemaHallMapper.toEntity(createCinemaHallRequest);
        createSeats(cinemaHall);
        return CinemaHallMapper.toResponse(cinemaHallRepository.save(cinemaHall));
    }

    private void createSeats(CinemaHall cinemaHall) {
        for (int row = 1; row <= cinemaHall.getRows(); row++) {
            for (int seatNumber = 1; seatNumber <= cinemaHall.getSeatsPerRow(); seatNumber++) {
                Seat seat = new Seat();
                seat.setRowNumber(row);
                seat.setSeatNumber(seatNumber);
                cinemaHall.addSeat(seat);
            }
        }
    }

    @Transactional
    public void deleteCinemaHall(Long id) {
        CinemaHall cinemaHall = getCinemaHallOrThrow(id);
        cinemaHallRepository.delete(cinemaHall);
    }

}
