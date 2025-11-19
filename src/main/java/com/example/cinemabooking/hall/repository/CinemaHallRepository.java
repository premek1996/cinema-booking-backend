package com.example.cinemabooking.hall.repository;

import com.example.cinemabooking.hall.entity.CinemaHall;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {

    @NonNull
    @EntityGraph(attributePaths = "seats")
    List<CinemaHall> findAll();

    @NonNull
    @EntityGraph(attributePaths = "seats")
    Optional<CinemaHall> findById(@NonNull Long id);

    Optional<CinemaHall> findByName(String name);

}
