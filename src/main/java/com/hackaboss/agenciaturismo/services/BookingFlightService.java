package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.UpdateBookingFlightDto;
import com.hackaboss.agenciaturismo.model.BookFlight;
import com.hackaboss.agenciaturismo.model.Flight;
import com.hackaboss.agenciaturismo.model.User;
import com.hackaboss.agenciaturismo.repositories.BookingFlightRepository;
import com.hackaboss.agenciaturismo.repositories.FlightRepository;
import com.hackaboss.agenciaturismo.dto.BookingFlightDto;
import com.hackaboss.agenciaturismo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingFlightService implements IBookingFlightService {
    @Autowired
    private BookingFlightRepository bookingFlightRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private IUserService userService;

    @Override
    public BookFlight createBookingFlight(BookingFlightDto bookingFlightDto) {
        BookFlight bookFlight = new BookFlight();
        List<User> users = new ArrayList<>();


        Flight flightExist = flightRepository.findByFlightNumber(bookingFlightDto.getFlightCode());

        if (flightExist == null) {
            throw new IllegalArgumentException("The flight has not yet been registered in our database");
        }

        //Comprobamos que el vuelo esté eliminado de la forma lógica
        if (!flightExist.getBookFlights().isEmpty() && flightExist.isStatus()) {

            throw new IllegalArgumentException("The flight does not exist in the database");
        }


        //comprobamos la fecha
        if (!flightExist.getDate().isEqual(bookingFlightDto.getDate())) {
            throw new IllegalArgumentException("the date does not match this flight");
        }

        // Comprobamos que el usuario existe, si no cre un nuevo usuario
        for (User userDto : bookingFlightDto.getPassengers()) {
            User user = userService.findByEmail(userDto.getEmail());

            if (user == null) {
                // Si el usuario no existe, crea uno nuevo
                user = new User();
            }

            // Actualizar datos del usuario
            user.setName(userDto.getName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPassPort(userDto.getPassPort());
            user.setAge(userDto.getAge());

            // Guardar o actualizar el usuario
            userService.createUser(user);

            users.add(user);
        }


        // Verificamos la fecha y si la fecha está bien, seteo hacia la base de datos
        LocalDate currentDate = LocalDate.now();
        //Comprobamos que los mismos usuarios no puedan tener la misma reserva
        if (bookingFlightDto.getFlightCode().equals(flightExist.getFlightNumber()) && !bookingFlightDto.getSeatType().equals(bookFlight.getSeatType()) &&
        bookingFlightDto.getPassengers() != bookFlight.getPassengers()) {

            if (bookingFlightDto.getDate().isAfter(currentDate) || bookingFlightDto.getDate().isEqual(currentDate)) {
                // La fecha de reserva es válida
                bookFlight.setFlightCode(flightExist.getFlightNumber());
                bookFlight.setOrigin(flightExist.getOrigin());
                bookFlight.setDestination(flightExist.getDestination());
                bookFlight.setPeopleQ(users.size()); // Se entiende que cada persona tiene su propio billete de avión, ya que el billete es intransferible
                bookFlight.setSeatType(bookingFlightDto.getSeatType());
                bookFlight.setDate(bookingFlightDto.getDate());

                double priceTotal = (users.size() * bookingFlightDto.getPrice());

                bookFlight.setPrice(priceTotal);
                bookFlight.setPassengers(users); // Solo establecer una vez
                bookFlight.setFlight(flightExist);
                bookFlight.setStatus(false);

                // Comprobar si algún usuario ya tiene una reserva activa para la fecha del vuelo
                List<BookFlight> activeBookings = bookingFlightRepository.getUserActiveBookings(users, bookingFlightDto.getDate());


                // Comprobar si algún usuario ya tiene una reserva activa para la fecha del vuelo
                if (!activeBookings.isEmpty() && activeBookings.stream().anyMatch(BookFlight::isStatus)) {
                    throw new IllegalArgumentException("At least one user already has an active reservation for the specified date.");
                } else {

                    return bookingFlightRepository.save(bookFlight);
                }


            }else {
                throw new IllegalArgumentException("The flight date is not valid");
            }


        } else {
            throw new IllegalArgumentException("The flight does not exist in the database");
        }

    }


    @Override
    public List<BookFlight> getAllBookFlights() {
        List<BookFlight> bookFlights = bookingFlightRepository.findAll();
        List<BookFlight> filterBookFlights = new ArrayList<>();

        if (bookFlights.stream().anyMatch(bookFlight -> !bookFlight.isStatus())) {
            for (BookFlight bookFlight : bookFlights) {
                if (!bookFlight.isStatus()) {
                    filterBookFlights.add(bookFlight);
                }
            }
            return filterBookFlights;
        }else {
            throw new IllegalArgumentException("There are no available booked flights in the database.");

        }

    }

    @Override
    public BookFlight getFlightById(Long id) {
        Optional<BookFlight> optionalBookFlight= bookingFlightRepository.findById(id);

        //Comprobamos si existe la reserva
        if (optionalBookFlight.isPresent()) {
            BookFlight bookFlight = optionalBookFlight.get();


            if (bookFlight.isStatus()) {//reserva eliminada no podemos mostrarla
                throw new IllegalArgumentException("There are no reservations in the database.");
            }

        }else {
            throw new IllegalArgumentException("The booked flight reservation does not exist");
        }

        return bookingFlightRepository.findById(id).orElse(null);
    }

    @Override
    public BookFlight deleteBookingFlightById(Long id) {

        //Verificamos si existe la reserva para poder eliminarla
        Optional<BookFlight> optionalBookFlight = bookingFlightRepository.findById(id);

        if (optionalBookFlight.isPresent()) {
            BookFlight bookFlight = optionalBookFlight.get();

            if (bookFlight.isStatus()) {
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }

            //cambiar el estado y la fecha de borrado
            bookFlight.setStatus(true);
            bookFlight.setUpdated(LocalDate.now());
            return bookingFlightRepository.save(bookFlight);
        }else{
            throw new IllegalArgumentException("The flight reservation does not exist");
        }

    }

    @Override
    public BookFlight editBookingFlightById(Long id, UpdateBookingFlightDto bookingFlightDto) {
        Optional<BookFlight> optionalBookFlight = bookingFlightRepository.findById(id);

        //verificar si existe la reserva para poder actualizarla
        if(optionalBookFlight.isPresent()){
            BookFlight bookingFlight = optionalBookFlight.get();

            if (bookingFlight.isStatus()) {
                throw new IllegalArgumentException("The reservation is not found in the database.");
            }


            // Sumar 100 al precio actualizado

            if (!bookingFlightDto.getSeatType().equals(bookingFlight.getSeatType())) {
                if (bookingFlightDto.getSeatType().equals("Economy")) {
                    double newPriceEconomy = bookingFlight.getPrice() - 100;
                    if (newPriceEconomy < 50) {
                        throw new IllegalArgumentException("The type of seat cannot be less than the stipulated price. 50 €");
                    }
                    bookingFlight.setPrice(newPriceEconomy);
                    bookingFlight.setSeatType(bookingFlightDto.getSeatType());

                }
                if (bookingFlightDto.getSeatType().equals("Business")) {
                    double newPriceBusiness = bookingFlight.getPrice() + 100;
                    bookingFlight.setPrice(newPriceBusiness);
                    bookingFlight.setSeatType(bookingFlightDto.getSeatType());
                }
            } else {
                throw new IllegalArgumentException("The seat type is not valid. It already exists, choose: \"Business and Economy\"");
            }




            return bookingFlightRepository.save(bookingFlight);
        }else {
            throw new IllegalArgumentException("The reservation does not exist");
        }



    }


}
