package com.modsen.driver.repo;

import com.modsen.driver.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    @Query("SELECT d FROM Driver d WHERE d.isDeleted = false")
    Page<Driver> findAllNonDeleted(Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.id = :id and d.isDeleted = false")
    Optional<Driver> findDriverByIdNonDeleted(@Param("id") UUID id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdAndIsDeletedFalse(UUID id);

}
