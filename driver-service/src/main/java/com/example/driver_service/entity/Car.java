package com.example.driver_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "car")
@SQLDelete(sql = "UPDATE car SET is_deleted = true WHERE id = ?")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "mark")
    private String mark;

    @Column(name = "colour")
    private String colour;

    @OneToOne(mappedBy = "car")
    private Driver driver;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

}
