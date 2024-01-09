package com.hackaboss.agenciaturismo.controller;


import com.hackaboss.agenciaturismo.services.IFligthService;
import com.hackaboss.agenciaturismo.dto.FlightDto;
import com.hackaboss.agenciaturismo.model.Flight;
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
import java.util.List;

@RestController
@RequestMapping("/agency")
public class FlightController {

    @Autowired
    private IFligthService fligthService;



    @Operation(summary = "Create a new flight",
            description = "This method handles the creation of a new flight. It returns different HTTP response codes depending on the operation's result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid flight data or the flight creation date cannot be in the past."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/flight/new")
    public ResponseEntity<?> createFlight(@RequestBody FlightDto flightDto) {

        try {
            if (!flightDto.getOrigin().matches("[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+") || !flightDto.getDestination().matches("[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+")) {
                return ResponseEntity.badRequest().body("Flight origin and destination can only contain letters and cannot be empty");
            }

            Flight flight = fligthService.createFlight(flightDto);
            if (flight == null) {
                return ResponseEntity.badRequest().body("The flight creation date cannot be in the past");
            }
            return ResponseEntity.ok().body("Successfully created flight");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Get all flights by place and dates",
            description = "This method returns all existing flights that match the provided place and dates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all flights."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flights found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flights/search")
    public ResponseEntity<?> getAllFlightsByPlaceAndDates(@RequestParam LocalDate date1,
                                                          @RequestParam LocalDate date2,
                                                          @RequestParam String origin,
                                                          @RequestParam String destination) {
        try {
            List<Flight> flights = fligthService.getAllFlightsByPlaceAndDates(date1, date2, origin, destination);
            return ResponseEntity.ok().body(flights);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @Operation(summary = "Get all flights",
            description = "This method returns all existing flights.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all flights."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flights found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flights")
    public ResponseEntity<?> getAllFlights() {

        try {
            List<Flight> flights = fligthService.getAllFlights();
            return ResponseEntity.ok().body(flights);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get a flight by ID",
            description = "This method returns information about a specific flight based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight information."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flights/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable String id) {
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body("Please enter a valid hotel ID (numbers only)");
            }

            Long flightId = Long.valueOf(id);

            Flight fligth = fligthService.getFlightById(flightId);


            return ResponseEntity.ok().body(fligth);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Edit a flight by ID",
            description = "This method handles the modification of an existing flight. It returns different HTTP response codes depending on the operation's result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/flights/edit/{id}")
    public ResponseEntity<?> editFlightById(@PathVariable String id, @RequestBody FlightDto flightDto) {

        try {
            if (!flightDto.getOrigin().matches("[a-zA-ZáéíóúüñÁÉ ]+") ||!flightDto.getDestination().matches("[a-zA-Záéíóú ]+")){
                return ResponseEntity.badRequest().body("Flight origin and destination can only contain letters and cannot be empty");
            }

            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body("Please enter a valid flight ID (numbers only)");
            }

            Long flightId = Long.valueOf(id);

            fligthService.editFlightById(flightId, flightDto);

            return ResponseEntity.ok().body("Flight updated");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Delete a flight by ID",
            description = "This method handles the deletion of an existing flight. It returns different HTTP response codes depending on the operation's result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/flights/{id}")
    public ResponseEntity<?> deleteFlightById(@PathVariable String id) {

        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body("Please enter a valid flight ID (numbers only)");
            }
            Long flightIdDelete = Long.valueOf(id);

            fligthService.deleteFlightById(flightIdDelete);
            return ResponseEntity.ok().body("Flight deleted");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


    @Operation(summary = "Handle HTTP Message Not Readable Exception",
            description = "This method handles the exception that occurs when the HTTP message is not readable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Error when entering the date format, it must be yyyy-MM-dd."),
            @ApiResponse(responseCode = "500", description = "Error processing request")})

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Obtengo la causa raíz de la excepción
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }


}
