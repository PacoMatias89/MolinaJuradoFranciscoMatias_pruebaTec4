package com.hackaboss.agenciaturismo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Room {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String roomType;
    private Double roomPrice;
    private LocalDate disponibilityDateFrom;
    private LocalDate disponibilityDateTo;

    @OneToOne
    @JoinColumn(name = "hotel_id", unique = true)
    @JsonBackReference
    private Hotel hotel;



}
