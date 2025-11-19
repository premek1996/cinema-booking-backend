package com.example.cinemabooking.screening.repository;

import com.example.cinemabooking.screening.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
}
