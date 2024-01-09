package com.hackaboss.agenciaturismo.dto;

import com.hackaboss.agenciaturismo.model.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingHotelDto {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String place;
    private String hotelCode;
    private String roomType;
    private List<User>hosts;
}
