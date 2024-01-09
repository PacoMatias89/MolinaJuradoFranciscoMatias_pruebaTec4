package com.hackaboss.agenciaturismo.dto;

import lombok.*;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateFligthDto {

    private String seatType;
    private LocalDate date;



}
