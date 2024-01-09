package com.hackaboss.agenciaturismo.dto;


import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateBookingHotelDto {
    private LocalDate dateFrom;
    private LocalDate dateTo;

}
