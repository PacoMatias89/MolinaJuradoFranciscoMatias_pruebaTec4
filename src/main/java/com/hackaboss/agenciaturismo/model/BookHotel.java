package com.hackaboss.agenciaturismo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BookHotel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String hotelCode;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private int nights;
    private int peopleQ;
    private Double price;
    private String roomType;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = @JoinColumn(name = "bookedHotel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "hotelId")
    private Hotel hotel;

    @Column(name = "booking_hotel_delete")
    private boolean status;

    @Column(name = "booking_hotel_date_delete")
    private LocalDate updated;
}
