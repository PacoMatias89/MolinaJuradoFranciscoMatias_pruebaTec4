package com.hackaboss.agenciaturismo.controller;

import com.hackaboss.agenciaturismo.dto.UpdateBookingHotelDto;
import com.hackaboss.agenciaturismo.model.Room;
import com.hackaboss.agenciaturismo.model.User;
import com.hackaboss.agenciaturismo.services.IBookingHotelService;
import com.hackaboss.agenciaturismo.dto.BookingHotelDto;
import com.hackaboss.agenciaturismo.model.BookHotel;
import com.hackaboss.agenciaturismo.services.IRoomService;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class BookingHotelController {
    private static final String TEXT_PATTERN = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+";
    private static final String ERROR_CODE = "Please enter a valid hotel ID (numbers only)";
    private static final String INVALID_PLACE_NAME = "Invalid place name";
    private static final String INVALID_ROOM_TYPE = "Invalid room type";
    private static final String INVALID_USER_NAME = "Invalid user name";
    private static final String INVALID_USER_LASTNAME = "Invalid user lastname";
    private static final String INVALID_USER_EMAIL = "Invalid user email";
    private static final String INVALID_USER_PASSPORT = "Invalid user passport";
    private static final String INVALID_USER_AGE = "Invalid user age";
    private static final String DATE_ORDER_ERROR_START = "Error: Disponibility start date must be before end date.";
    private static final String DATE_ORDER_ERROR_END = "Error: Disponibility end date must be after start date.";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSPORT_PATTERN = "^[A-Za-z0-9]{6,15}$";


    @Autowired
    private IBookingHotelService bookingHotelService;

    @Autowired
    private IRoomService roomService;



    @Operation(summary = "Create a hotel reservation",
            description = "This method handles the creation of a hotel reservation. Returns different HTTP response codes depending on the operation result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid hotel reservation data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/hotel-booking/new")
    public ResponseEntity<?> createBookHotel(@RequestBody BookingHotelDto bookingHotelDto) {
        // Validar la fecha y otros datos antes de intentar crear la reserva
        String validation = valitadeBookingDate(bookingHotelDto);

        if (validation != null) {
            return ResponseEntity.badRequest().body(validation);
        }

        // Realizar verificación antes de crear la reserva
        int peopleQ = bookingHotelDto.getHosts().size();
        String roomType = bookingHotelDto.getRoomType();
        int maxPeopleAllowed = 0;

        if (roomType.equalsIgnoreCase("doble")) {
            maxPeopleAllowed = 2;
        } else if (roomType.equalsIgnoreCase("triple")) {
            maxPeopleAllowed = 3;
        } else if (roomType.equalsIgnoreCase("individual")) {
            maxPeopleAllowed = 1;
        } else {
            // Tipo de habitación no reconocido
            return ResponseEntity.badRequest().body("Invalid room type. Unable to complete the reservation.");
        }

        // Verificar si la cantidad de personas es mayor que la permitida
        if (peopleQ > maxPeopleAllowed) {
            return ResponseEntity.badRequest().body("The " + roomType + " room allows a maximum of " + maxPeopleAllowed + " people.");
        }
        //validamos los datos que hay dentro de la base de datos
        try {

            // Intentar crear la reserva
            bookingHotelService.createBookHotel(bookingHotelDto);

            int nights = (int) ChronoUnit.DAYS.between(bookingHotelDto.getDateFrom(), bookingHotelDto.getDateTo());

            // Buscamos el precio de la habitación a través del hotel y comprobamos que la habitación esté en el hotel
            List<Room> rooms = roomService.getAllRooms();

            // Buscamos la habitación correspondiente al tipo y al hotel
            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getHotelCode().equals(bookingHotelDto.getHotelCode()) &&
                            room.getRoomType().equalsIgnoreCase(bookingHotelDto.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .findFirst()
                    .orElse(0.0);

            if (totalPrice == 0.0) {
                return ResponseEntity.badRequest().body("Unable to find room for the given hotel and room type.");
            }

            return ResponseEntity.ok().body("The total price of the reservation is: " + totalPrice + " €");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get all hotel reservations",
            description = "This method returns all existing hotel reservations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all hotel reservations."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel reservations found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotel-booking/all")
    public ResponseEntity<?> getAllBookHotels() {
        try {
            List<BookHotel> bookHotels = bookingHotelService.getAllBookHotels();
            return ResponseEntity.ok().body(bookHotels);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get a hotel reservation by ID",
            description = "This method returns information about a specific hotel reservation based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation information."),
            @ApiResponse(responseCode = "400", description = "Invalid request or hotel reservation not found."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotel-booking/get/{id}")
    public ResponseEntity<?> getBookHotelById(@PathVariable String id) {
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            BookHotel bookHotel = bookingHotelService.getBookHotelById(Long.parseLong(id));
            return ResponseEntity.ok().body(bookHotel);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Edit a hotel reservation",
            description = "This method handles the modification of a hotel reservation. Returns different HTTP response codes depending on the operation result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid hotel reservation data or unable to find room."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/hotel-booking/edit/{id}")
    public ResponseEntity<?> editBookHotel(@PathVariable String id, @RequestBody UpdateBookingHotelDto bookingHotelDto) {
        // Realizar verificación antes de editar la reserva
        try {
            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest().body(ERROR_CODE);
            }
            List<Room> rooms = roomService.getAllRooms();

            Long bookingId = Long.parseLong(id);

            //Obtenemos los datos que hay en la base de datos antes de actualizar
            int currentNights = bookingHotelService.getAllBookHotels().get(0).getNights();
            Double originalPrice = bookingHotelService.getAllBookHotels().get(0).getPrice();

            BookHotel bookHotel = bookingHotelService.editBookHotelById(bookingId, bookingHotelDto);

            LocalDate dateFrom = bookingHotelDto.getDateFrom();
            LocalDate dateTo = bookingHotelDto.getDateTo();

            int nights = (int) ChronoUnit.DAYS.between(dateFrom, dateTo);


            double totalPrice = rooms.stream()
                    .filter(room -> room.getHotel().getHotelCode().equals(bookHotel.getHotelCode()) &&
                            room.getRoomType().equalsIgnoreCase(bookHotel.getRoomType()))
                    .mapToDouble(room -> nights * room.getRoomPrice())
                    .findFirst()
                    .orElse(0.0);

            //Hacemos los cálculos para el nuevo precio, ya que actualizamos las fechas
            // y consigo las noches que se van a quedar
            // Verificación de las noches antes de realizar cálculos
            if (nights < currentNights) {
                double updatePrice = originalPrice - totalPrice;
                String message = getReservationDetails(bookHotel)+ '\n'+
                        "The amount of money to be returned to the client is: " + updatePrice ;
                return ResponseEntity.ok().body(message);
            }

            if (nights > currentNights) {
                double updatePrice = totalPrice - originalPrice;
                String message = getReservationDetails(bookHotel)+ '\n'+
                        "The additional cost to pay is: " + updatePrice ;
                return ResponseEntity.ok().body(message);
            }

            // Actualización de las noches y el precio
            bookHotel.setNights(nights);
            bookHotel.setPrice(totalPrice);

            String message = "Updated reservation: \n" + getReservationDetails(bookHotel);

            return ResponseEntity.ok().body(message);


        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // Método para obtener detalles de la reserva
    private String getReservationDetails(BookHotel bookHotel) {
        // Devuelvo la información específica de la reserva
        return "Reservation details: " +
                "\nHotel Code: " + bookHotel.getHotelCode() +
                "\nRoom Type: " + bookHotel.getRoomType() +
                "\nNights: " + bookHotel.getNights() +
                "\nTotal Price: " + bookHotel.getPrice();
    }

    @Operation(summary = "Delete a hotel reservation by ID",
            description = "This method deletes a specific hotel reservation based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request or no hotel reservation found with the given ID."),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/hotel-booking/delete/{id}")
    public ResponseEntity<?> deleteBookHotel(@PathVariable String id) {

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body(ERROR_CODE);
        }

        Long bookingId = Long.parseLong(id);

        try {
            bookingHotelService.deleteBookHotelById(bookingId);
            return ResponseEntity.ok().body("Reservation deleted");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    /**
     * Métodos que se encargan de validar los parámetros que entran por el DTO.
     * En un proyecto con Fronted, por norma general son estos lo que hacen las validaciones
     * pertinentes, pero al no haber fronted, pues las hago de esta manera.
     */


    private String valitadeBookingDate(BookingHotelDto bookingHotelDto) {

        if (bookingHotelDto.getDateFrom() == null || bookingHotelDto.getDateTo() == null) {
            return "Date data is required";
        }

        if (bookingHotelDto.getPlace() == null || !bookingHotelDto.getPlace().matches(TEXT_PATTERN)) {
            return INVALID_PLACE_NAME;
        }

        if (bookingHotelDto.getRoomType() == null || !bookingHotelDto.getRoomType().matches(TEXT_PATTERN)) {
            return INVALID_ROOM_TYPE;
        }

        if (bookingHotelDto.getDateFrom().isAfter(bookingHotelDto.getDateTo())) {
            return DATE_ORDER_ERROR_START;
        }

        if (bookingHotelDto.getDateTo().isAfter(bookingHotelDto.getDateTo())) {
            return DATE_ORDER_ERROR_END;
        }

        return valitadeBookingUser(bookingHotelDto.getHosts());
    }

    //Valido al usuario
    private String valitadeBookingUser(List<User> users) {

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Obtén la causa raíz de la excepción
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: processing request");
    }

}

