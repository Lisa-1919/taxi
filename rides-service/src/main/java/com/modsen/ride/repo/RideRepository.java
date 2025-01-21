package com.modsen.ride.repo;

import com.modsen.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    boolean existsByIdAndDriverId(Long id, UUID driverId);

    boolean existsByIdAndPassengerId(Long id, UUID passengerId);
}
