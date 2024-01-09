package com.hackaboss.agenciaturismo.controller;

import com.hackaboss.agenciaturismo.dto.UpdateBookingFlightDto;
import com.hackaboss.agenciaturismo.model.User;
import com.hackaboss.agenciaturismo.services.IBookingFlightService;
import com.hackaboss.agenciaturismo.dto.BookingFlightDto;
import com.hackaboss.agenciaturismo.model.BookFlight;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class BookingFlightController {

    private static final String TEXT_PATTERN = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+";
    private static final String ERROR_CODE = "Please enter a valid hotel ID (numbers only)";

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSPORT_PATTERN = "^[A-Za-z0-9]{6,15}$";
    private static final String INVALID_USER_NAME = "Invalid user name";
    private static final String INVALID_USER_LASTNAME = "Invalid user lastname";
    private static final String INVALID_USER_EMAIL = "Invalid user email";
    private static final String INVALID_USER_PASSPORT = "Invalid user passport";
    private static final String INVALID_USER_AGE = "Invalid user age";
    private static final String ERROR_CODE_NULL = "Flight code is required";
    private static final String ERROR_SEATTYPE_ERROR = "Error: Seat type does not match.";

    @Autowired
    private IBookingFlightService bookingFlightService;

    @Operation(summary = "Create a flight reservation",
            description = "This method handles the creation of a flight reservation. Returns different HTTP response codes depending on the operation result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/flight-booking/new")
    public ResponseEntity<?> createBookFlight(@RequestBody BookingFlightDto bookingFlightDto) {
        try {
            String validationResult = validator(bookingFlightDto);
            if (validationResult != null) {
                return ResponseEntity.badRequest().body(validationResult);
            }

            bookingFlightService.createBookingFlight(bookingFlightDto);

            Double totalPrice = bookingFlightDto.getPrice() * bookingFlightDto.getPassengers().size();

            return ResponseEntity.ok().body("The total price of the flight is: " + totalPrice);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update a flight reservation",
            description = "This method handles the update of a flight reservation. Returns different HTTP response codes depending on the operation result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/flight-booking/edit/{id}")
    public ResponseEntity<?> editBookFlight(@PathVariable String id, @RequestBody UpdateBookingFlightDto bookingFlightDto) {
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }

            Long bookingFlightId = Long.valueOf(id);

            BookFlight bookFlight = bookingFlightService.editBookingFlightById(bookingFlightId, bookingFlightDto);

            if (bookFlight == null) {
                return ResponseEntity.badRequest().body("Cannot edit a flight with the same reservation");
            }

            Double totalPrice = bookFlight.getPrice();
            return ResponseEntity.ok().body("The total price of the flight is: " + totalPrice);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get all flight reservations",
            description = "This method returns all existing flight reservations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all flight reservations."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight reservations found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flight-booking/all")
    public ResponseEntity<?> getAllBookFlights() {
        try {
            List<BookFlight> allBookFlights = bookingFlightService.getAllBookFlights();
            return ResponseEntity.ok().body(allBookFlights);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get a flight reservation by ID",
            description = "This method returns information about a specific flight reservation based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation found successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight reservation found with the given ID."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flight-booking/get/{id}")
    public ResponseEntity<?> getBookFlight(@PathVariable String id) {
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }

            Long bookingFlightId = Long.valueOf(id);

            BookFlight bookFlight = bookingFlightService.getFlightById(bookingFlightId);

            return ResponseEntity.ok().body(bookFlight);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a flight reservation by ID",
            description = "This method deletes a specific flight reservation based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no flight reservation found with the given ID."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/flight-booking/delete/{id}")
    public ResponseEntity<?> deleteBookFlight(@PathVariable String id) {

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body(ERROR_CODE);
        }

        // parseo el ID para convertirlo a Long y poder eliminarlo si
        // no está ya eliminado
        Long flightId = Long.valueOf(id);

        try {
            bookingFlightService.deleteBookingFlightById(flightId);
            return ResponseEntity.ok().body("the reservation has been successfully removed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Métodos responsables de validar parámetros del DTO.
     * En un proyecto con frontend, las validaciones generalmente las maneja el frontend,
     * pero a falta de una interfaz, las validaciones se realizan de esta manera.
     */
    public String validator(BookingFlightDto bookingFlightDto) {
        if (bookingFlightDto.getDate() == null) {
            return "Date data is required";
        }

        if (bookingFlightDto.getFlightCode() == null) {
            return ERROR_CODE_NULL;
        }

        String seatType = bookingFlightDto.getSeatType();

        if (!seatType.matches(TEXT_PATTERN)) {
            return ERROR_SEATTYPE_ERROR;
        }

        double price = bookingFlightDto.getPrice();
        if (price <= 0) {
            return "Price cannot be negative or 0";
        }
        return validatorUserFlight(bookingFlightDto.getPassengers());
    }

    private String validatorUserFlight(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "User information is required";
        }

        if (users.get(0).getName() == null || !users.get(0).getName().matches(TEXT_PATTERN)) {
            return INVALID_USER_NAME;
        }

        if (users.get(0).getLastName() == null || !users.get(0).getLastName().matches(TEXT_PATTERN)) {
            return INVALID_USER_LASTNAME;
        }

        if (users.get(0).getEmail() == null || !users.get(0).getEmail().matches(EMAIL_PATTERN)) {
            return INVALID_USER_EMAIL;
        }

        if (users.get(0).getPassPort() == null || !users.get(0).getPassPort().matches(PASSPORT_PATTERN)) {
            return INVALID_USER_PASSPORT;
        }

        if (users.get(0).getAge() <= 0) {
            return INVALID_USER_AGE;
        }

        return null;
    }


    @Operation(summary = "Handle HTTP Message Not Readable Exception",
            description = "This method handles the exception that occurs when the HTTP message is not readable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Error when entering the date format, it must be yyyy-MM-dd."),
            @ApiResponse(responseCode = "500", description = "Error processing request")})
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Get the root cause of the exception
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }
}
