package com.example.rides_service.repo;

import com.example.rides_service.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    boolean existsByIdAndDriverId(Long id, Long driverId);

    boolean existsByIdAndPassengerId(Long id, Long passengerId);
}
