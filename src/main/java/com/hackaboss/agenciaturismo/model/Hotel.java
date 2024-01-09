package com.hackaboss.agenciaturismo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIgnoreProperties({"bookHotels"})
public class Hotel {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "hotel_code")
    private String hotelCode;

    @Column(name = "hotel_name")
    private String name;

    @Column(name = "hotel_place")
    private String place;

    @Column(name = "is_booked")
    private boolean isBooked;

    @Column(name = "hotel_delete")
    private boolean status;

    @Column(name = "hotel_date_delete")
    private LocalDate updated;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<BookHotel> bookHotels = new ArrayList<>();

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL)
    private Room room;

}
