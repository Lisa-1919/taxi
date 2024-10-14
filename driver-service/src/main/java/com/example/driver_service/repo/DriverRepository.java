package com.example.driver_service.repo;

import com.example.driver_service.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d WHERE d.isDeleted = false")
    Page<Driver> getAllNonDeleted(Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.id = :id and d.isDeleted = false")
    Optional<Driver> getDriverByIdNonDeleted(@Param("id") Long id);

}
