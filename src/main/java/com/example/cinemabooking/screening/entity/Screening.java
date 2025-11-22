package com.example.cinemabooking.screening.entity;

import com.example.cinemabooking.common.jpa.BaseEntity;
import com.example.cinemabooking.hall.entity.CinemaHall;
import com.example.cinemabooking.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@SuperBuilder
@Entity
@Table(
        name = "screenings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hall_id", "start_time"})
)
public class Screening extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall cinemaHall;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

}
