package com.hackaboss.agenciaturismo.dto;
import com.hackaboss.agenciaturismo.model.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class BookingFlightDto {


    private LocalDate date;
    private String origin;
    private String destination;
    private String flightCode;
    private String seatType;
    private double price;
    private List<User> passengers;

}
