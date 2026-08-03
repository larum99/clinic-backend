package com.clinic.clinic.repository;

import com.clinic.clinic.entity.DentalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DentalServiceRepository extends JpaRepository<DentalService, Long> {
    boolean existsByName(String name);
    Optional<DentalService> findByName(String name);
}
