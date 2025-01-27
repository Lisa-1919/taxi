package com.modsen.passenger.repo;

import com.modsen.passenger.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, UUID> {

    @Query("SELECT p FROM Passenger p WHERE p.isDeleted = false")
    Page<Passenger> findAllNonDeleted(Pageable pageable);

    @Query("SELECT p FROM Passenger p WHERE p.id = :id and p.isDeleted = false")
    Optional<Passenger> findPassengerByIdNonDeleted(@Param("id") UUID id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdAndIsDeletedFalse(UUID id);
}
