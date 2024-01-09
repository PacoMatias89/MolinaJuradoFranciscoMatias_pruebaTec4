package com.hackaboss.agenciaturismo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateBookingFlightDto {
    private String seatType;
    private Double price;
}
