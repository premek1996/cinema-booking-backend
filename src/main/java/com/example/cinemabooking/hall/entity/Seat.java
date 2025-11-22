package com.example.cinemabooking.hall.entity;

import com.example.cinemabooking.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@SuperBuilder
@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"hall_id", "row_number", "seat_number"}))
public class Seat extends BaseEntity {

    @Column(name = "row_number", nullable = false)
    private int rowNumber;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall cinemaHall;

}
