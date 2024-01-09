package com.hackaboss.agenciaturismo.controller;

import com.hackaboss.agenciaturismo.dto.HotelDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotelControllerTest {

    @Autowired
    private HotelController hotelController;

    @Test
    void addHotel_validHotelDto_returnsSuccess() {

        // Arrange
        HotelDto hotelDto = new HotelDto("Hotel Prueba Test", "Málaga", "Doble",450.0, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 3));
        // Act
        ResponseEntity<?> response = hotelController.addHotel(hotelDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully created hotel", response.getBody());
    }

    @Test
    void addHotel_invalidHotelDto_returnsError() {
        // Arrange
        HotelDto hotelDto = new HotelDto("Hotel Prueba Test", "Málaga", "Doble", -450.0, LocalDate.of(2023, 6, 1), LocalDate.of(2024, 8, 3));

        // Act
        ResponseEntity<?> response = hotelController.addHotel(hotelDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<String> errors = (List<String>) response.getBody();
        assertEquals(2, errors.size());
        assertTrue(errors.contains("Check that the dates are correct"));
        assertTrue(errors.contains("Room price must be greater than 0"));
    }


    @Test
    void addHotel_invalidHotelDto_returnsBadRequest(){
        // Arrange
        HotelDto hotelDto = new HotelDto("Hotel Prueba Test", "Málaga", "Doble", 0.0, LocalDate.of(2023, 1, 3), LocalDate.of(2023, 1, 1));

        // Act
        ResponseEntity<?> response = hotelController.addHotel(hotelDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof List<?>);
        List<?> errors = (List<?>) response.getBody();
        assertEquals(2, errors.size());
        assertTrue(errors.contains("Check that the dates are correct"));
        assertTrue(errors.contains("Room price must be greater than 0"));
    }



}