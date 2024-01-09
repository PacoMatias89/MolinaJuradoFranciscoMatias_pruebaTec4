package com.hackaboss.agenciaturismo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String passPort;
    private int age;

    @ToString.Exclude
    @ManyToMany(mappedBy = "passengers", cascade = CascadeType.ALL)
    private List<BookFlight> bookFlights = new ArrayList<>();


    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<BookHotel> hotels = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", passPort='" + passPort + '\'' +
                ", age=" + age +
                '}';
    }

}
