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
public class BookFlight {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private LocalDate date;
    private String origin;
    private String destination;
    private String flightCode;
    private Integer peopleQ;
    private String seatType;
    private Double price;

    //Hago la relación entre tablas(User y booking_flight) de mucho a muchos

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "bookeFlight_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> passengers = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "id_flight")
    private Flight flight;

    @Column(name = "booking_flight_delete")
    private boolean status;

    @Column(name = "booking_flight_date_delete")
    private LocalDate updated;

    @Override

    public String toString() {
        return "BookFlight{" +
                "id=" + id +
                ", date=" + date +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", flightCode='" + flightCode + '\'' +
                ", peopleQ=" + peopleQ +
                ", seatType='" + seatType + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", updated=" + updated +
                '}';
    }
}
