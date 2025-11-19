package com.example.cinemabooking.screening.repository;

import com.example.cinemabooking.screening.entity.Screening;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"movie", "cinemaHall"})
    List<Screening> findAll();

    @NonNull
    @EntityGraph(attributePaths = {"movie", "cinemaHall"})
    Optional<Screening> findById(@NonNull Long id);

    @NonNull
    @EntityGraph(attributePaths = {"movie", "cinemaHall"})
    List<Screening> findByMovieId(@NonNull Long movieId);

    @NonNull
    @EntityGraph(attributePaths = {"movie", "cinemaHall"})
    List<Screening> findByCinemaHallId(@NonNull Long cinemaHallId);

    @NonNull
    @EntityGraph(attributePaths = {"movie", "cinemaHall"})
    List<Screening> findByStartTimeBetween(@NonNull LocalDateTime startOfDay, @NonNull LocalDateTime endOfDay);

    @Query("""
                SELECT COUNT(s) > 0 FROM Screening s
                WHERE s.cinemaHall.id = :hallId
                  AND s.startTime < :endTime
                  AND s.endTime > :startTime
            """)
    boolean existsTimeConflict(Long hallId, LocalDateTime startTime, LocalDateTime endTime);

}
