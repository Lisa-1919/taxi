package com.example.passenger_service.repo;

import com.example.passenger_service.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT p FROM Passenger p WHERE p.isDeleted = false")
    Page<Passenger> findAllNonDeleted(Pageable pageable);

    @Query("SELECT p FROM Passenger p WHERE p.id = :id and p.isDeleted = false")
    Optional<Passenger> findPassengerByIdNonDeleted(@Param("id") Long id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT COUNT(p) > 0 FROM Passenger p WHERE p.id = :id and p.isDeleted = false")
    boolean existsByIdAndNonDeleted(@Param("id") Long id);
}
