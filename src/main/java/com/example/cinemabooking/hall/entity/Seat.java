package com.example.cinemabooking.hall.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"hall_id", "rowNumber", "seatNumber"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rowNumber;

    @Column(nullable = false)
    private int seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall cinemaHall;

}
