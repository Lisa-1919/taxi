package com.example.driver_service.repo;

import com.example.driver_service.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.isDeleted = false")
    Page<Car> getAllNonDeleted(Pageable pageable);

    @Query("SELECT c FROM Car c WHERE c.id = :id and c.isDeleted = false")
    Optional<Car> getCarByIdNonDeleted(@Param("id") Long id);

}
