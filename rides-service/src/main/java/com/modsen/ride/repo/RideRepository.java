package com.modsen.ride.repo;

import com.modsen.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    boolean existsByIdAndDriverId(Long id, Long driverId);

    boolean existsByIdAndPassengerId(Long id, Long passengerId);
}
