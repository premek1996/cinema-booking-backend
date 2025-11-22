package com.example.cinemabooking.hall.entity;

import com.example.cinemabooking.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@SuperBuilder
@Entity
@Table(name = "cinema_halls")
public class CinemaHall extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "rows", nullable = false)
    private int rows;

    @Column(name = "seats_per_row", nullable = false)
    private int seatsPerRow;

    @OneToMany(mappedBy = "cinemaHall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Seat> seats = new HashSet<>();

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setCinemaHall(this);
    }

}
