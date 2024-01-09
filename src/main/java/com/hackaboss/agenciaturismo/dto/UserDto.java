package com.hackaboss.agenciaturismo.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private String name;
    private String lastName;
    private String email;
    private String passPort;
    private int age;

}
