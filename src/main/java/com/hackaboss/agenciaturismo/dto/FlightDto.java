package com.hackaboss.agenciaturismo.dto;

import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class FlightDto {
   private String origin;
   private String destination;
   private LocalDate date;

}
