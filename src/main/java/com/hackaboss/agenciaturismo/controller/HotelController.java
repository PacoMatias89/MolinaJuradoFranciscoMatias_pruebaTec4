package com.hackaboss.agenciaturismo.controller;

import com.hackaboss.agenciaturismo.services.IHotelService;
import com.hackaboss.agenciaturismo.dto.HotelDto;
import com.hackaboss.agenciaturismo.dto.UpdateHotelDto;
import com.hackaboss.agenciaturismo.model.Hotel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    private static final String ERROR_CODE ="Please enter a valid hotel ID (numbers only)";

    @Operation(summary = "Add a new hotel",
            description = "This method handles the creation of a new hotel. It returns different HTTP response codes depending on the operation's result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid hotel data or error creating hotel."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/hotels/new")
    public ResponseEntity<?> addHotel(@RequestBody HotelDto hotelDto) {
        List<String> validationErrors = validateHotelDto(hotelDto);
        try {


            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(validationErrors);
            }

            Hotel hotel = hotelService.createHotel(hotelDto);
            if (hotel == null) {
                return ResponseEntity.badRequest().body("Error creating hotel");
            }
            return ResponseEntity.ok().body("Successfully created hotel");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @Operation(summary = "Get hotels by place and date",
            description = "This method returns hotels based on the provided place, date range, and booking status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hotels."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotels found."),
            @ApiResponse(responseCode = "500", description = "Server error")})

    @GetMapping("/hotels/search")
    public ResponseEntity<?> getHotelsByPlaceAndDate(@RequestParam LocalDate disponibilityDateFrom,
                                        @RequestParam LocalDate disponibilityDateTo,
                                        @RequestParam String place,
                                        @RequestParam boolean isBooked) {
        try{
            List<Hotel> hotels = hotelService.getAllHotelsByPlaceDate(disponibilityDateFrom, disponibilityDateTo, place, isBooked);
            return ResponseEntity.ok().body(hotels);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @Operation(summary = "Get all hotels",
            description = "This method returns all existing hotels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all hotels."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotels found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotels")
    public ResponseEntity<?> getAllHotels() {
        try {
            List<Hotel> hotels = hotelService.getAllHotels();
            return ResponseEntity.ok().body(hotels);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get a hotel by ID",
            description = "This method returns information about a specific hotel based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel information."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotels/{id}")
    public ResponseEntity<?> getHotelById(@PathVariable String id) {
        try{
            // Verificar que el ID solo contenga números
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }

            // Convertir el ID a tipo Long
            Long hotelId = Long.valueOf(id);

            // Obtener el hotel por ID
            Hotel hotel = hotelService.getHotelById(hotelId);

            return ResponseEntity.ok().body(hotel);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


    @Operation(summary = "Edit a hotel by ID",
            description = "This method handles the modification of an existing hotel. It returns different HTTP response codes depending on the operation's result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel edited successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/hotels/edit/{id}")
    public ResponseEntity<?> editHotelById(@PathVariable String id, @RequestBody UpdateHotelDto hotelDto){

        try {
            //hacemos comprobaciones de entrada de datos
            if (!hotelDto.getPlace().matches("[a-zA-ZáéíóúüñÁ ]+")){
                return ResponseEntity.badRequest().body("place can only contain letters");
            }

            if (!id.matches("\\d+") ) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }

            Long hotelId = Long.valueOf(id);

            // Resto del código para editar el hotel
            hotelService.editHotelById(hotelId, hotelDto);
            return ResponseEntity.ok().body("Hotel edited");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Delete a hotel by ID",
            description = "This method handles the deletion of an existing hotel. It returns different HTTP response codes depending on the operation's result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<?> deleteHotelById(@PathVariable String id) {
        try{

            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            Long deletehotelbyId = Long.valueOf(id);

            hotelService.deleteHotelById(deletehotelbyId);
            return ResponseEntity.ok().body("Hotel deleted");

        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /**
     * Aquí válido la entrada de datos del dto para enviarlos
     * en el formato correcto a la base de datos
     * */

    private List<String> validateHotelDto(HotelDto hotelDto) {
        List<String> validationErrors = new ArrayList<>();

        validateField(validationErrors, "Hotel name", hotelDto.getName(), "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ0-9 ]+");
        validateField(validationErrors, "Room type", hotelDto.getRoomType(), "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+");
        validateField(validationErrors, "Hotel place", hotelDto.getPlace(), "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+");

        LocalDate currentDate = LocalDate.now();
        if (hotelDto.getDisponibilityDateTo().isBefore(hotelDto.getDisponibilityDateFrom())
                || hotelDto.getDisponibilityDateFrom().isBefore(currentDate)
                || hotelDto.getDisponibilityDateTo().isBefore(currentDate)) {
            validationErrors.add("Check that the dates are correct");
        }

        if (hotelDto.getRoomPrice() <= 0) {
            validationErrors.add("Room price must be greater than 0");
        }

        return validationErrors;
    }

    private void validateField(List<String> errors, String fieldName, String fieldValue, String regex) {
        if (fieldValue == null || fieldValue.isEmpty() || !fieldValue.matches(regex)) {
            errors.add(fieldName + " can only contain letters and cannot be empty");
        }


    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @Operation(summary = "Handle HTTP Message Not Readable Exception",
            description = "This method handles the HTTP Message Not Readable Exception and returns appropriate responses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Error when entering the date format or invalid request."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Obtén la causa raíz de la excepción
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }
}

