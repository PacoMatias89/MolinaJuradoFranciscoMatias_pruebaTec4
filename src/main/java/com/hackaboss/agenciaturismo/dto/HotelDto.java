package com.hackaboss.agenciaturismo.dto;

import com.hackaboss.agenciaturismo.model.Room;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class HotelDto {
    private String name;
    private String place;
    private String roomType;
    private Double roomPrice;
    private LocalDate disponibilityDateFrom;
    private LocalDate disponibilityDateTo;
    private List<Room> rooms;

    public HotelDto(String name, String place, String roomType, Double roomPrice, LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo) {
        this.name = name;
        this.place = place;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.disponibilityDateFrom = disponibilityDateFrom;
        this.disponibilityDateTo = disponibilityDateTo;
    }
}
