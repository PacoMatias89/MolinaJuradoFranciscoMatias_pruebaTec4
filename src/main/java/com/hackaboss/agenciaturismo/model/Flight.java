package com.hackaboss.agenciaturismo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@JsonIgnoreProperties({"bookFlights"})
public class Flight {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "flight_origin")
    private String origin;


    @Column(name = "flight_destination")
    private String destination;

    @Column(name = "flight_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(name = "flight_delete")
    private boolean status;

    @Column(name = "flight_date_delete")
    private LocalDate updated;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<BookFlight> bookFlights = new ArrayList<>();
}
