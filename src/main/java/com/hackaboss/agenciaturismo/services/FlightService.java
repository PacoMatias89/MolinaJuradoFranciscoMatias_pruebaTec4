package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.FlightDto;
import com.hackaboss.agenciaturismo.model.BookFlight;
import com.hackaboss.agenciaturismo.model.Flight;
import com.hackaboss.agenciaturismo.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class FlightService implements IFligthService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingFlightService bookingFlightService;

    private final Random random = new Random();

    @Override
    public Flight createFlight(FlightDto flightDto) {
        try {
            LocalDate currentDate = LocalDate.now();
            if (flightDto.getDate().isAfter(currentDate) || flightDto.getDate().isEqual(currentDate)) {

                Flight flight = new Flight();

                flight.setOrigin(flightDto.getOrigin());
                flight.setDestination(flightDto.getDestination());

                //Generamos el código del vuelo de forma aleatoria obteniendo las primeras letras del nombre
                String twoLettersOrigin = flight.getOrigin().isEmpty() ? "OO" : flight.getOrigin().substring(0, Math.min(2, flight.getOrigin().length()));
                String twoLettersDestination = flight.getDestination().isEmpty() ? "DD" : flight.getDestination().substring(0, Math.min(2, flight.getDestination().length()));

                // Generar los números del código de forma aleatoria
                String formattedNumber = String.format("%04d", random.nextInt(10000)); // Número entre 0 y 9999

                // Construir el código del vuelo
                String flightCode = twoLettersOrigin + twoLettersDestination + "-" + formattedNumber;
                flight.setFlightNumber(flightCode.toUpperCase());

                flight.setDate(flightDto.getDate());
                flight.setStatus(false);

                return flightRepository.save(flight);
            }
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot create flight because date cannot be created in the past");
        }
        return null;
    }

    @Override
    public List<Flight> getAllFlightsByPlaceAndDates(LocalDate availabilityDateFrom, LocalDate availabilityDateTo, String origin, String destination) {
        List<Flight> flights = flightRepository.getAllFlightDate(availabilityDateFrom, availabilityDateTo, origin, destination);
        if (flights.isEmpty() || flights.stream().anyMatch(flight -> flight.getBookFlights().stream().anyMatch(BookFlight::isStatus))) {
            throw new IllegalArgumentException("There are no flights with these criteria in the database.");

        }
        return flights;
    }

    @Override
    public List<Flight> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        List<Flight> filteredFlights = new ArrayList<>();

        if (flights.stream().anyMatch(flight -> !flight.isStatus())) {
            for (Flight flight : flights) {
                if (!flight.isStatus()) {
                    filteredFlights.add(flight);
                }
            }
            return filteredFlights;
        } else {
            throw new IllegalArgumentException("There are no available flights in the database.");
        }
    }


    @Override
    public Flight deleteFlightById(Long id) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);

        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();

            if (!flight.getBookFlights().isEmpty() && flight.getBookFlights().stream().anyMatch(BookFlight::isStatus)) {
                flight.setStatus(true);
                flight.setUpdated(LocalDate.now());
                return flightRepository.save(flight);

            }

            //El status del vuelo significa que el vuelo ha sido borrado
            if (flight.isStatus()) {
                throw new IllegalArgumentException("The flight cannot be deleted because it is not found in the database.");
            }

            if (!flight.getBookFlights().isEmpty()) {
                //Si el vuelo existe, devuelvo ese mismo vuelo
                throw new IllegalArgumentException("The flight has a reservation.");
            }



            flight.setStatus(true);
            flight.setUpdated(LocalDate.now());
            return flightRepository.save(flight);
        }else {
            throw new IllegalArgumentException("The flight is not found in the database.");
        }

    }


    @Override
    public Flight editFlightById(Long id, FlightDto flightDto) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);

        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();

            //Si el vuelo existe devolvemos una excepción
            if (!flight.getBookFlights().isEmpty()) {
                throw new IllegalArgumentException("The flight has a reservation.");
            }

            flight.setOrigin(flightDto.getOrigin());
            flight.setDestination(flightDto.getDestination());

            String twoLettersOrigin = flight.getOrigin().isEmpty() ? "OO" : flight.getOrigin().substring(0, Math.min(2, flight.getOrigin().length()));
            String twoLettersDestination = flight.getDestination().isEmpty() ? "DD" : flight.getDestination().substring(0, Math.min(2, flight.getDestination().length()));

            // Generar los números del código de forma aleatoria
            String formattedNumber = String.format("%04d", random.nextInt(10000)); // Número entre 0 y 9999

            // Construir el código del vuelo
            String flightCode = twoLettersOrigin + twoLettersDestination + "-" + formattedNumber;
            flight.setFlightNumber(flightCode.toUpperCase());

            flight.setDate(flightDto.getDate());
            flight.setOrigin(flightDto.getOrigin());
            flight.setDestination(flightDto.getDestination());

            //Comprobamos la fecha que no esté en el pasado
            if (flightDto.getDate().isBefore(LocalDate.now())){
                throw new IllegalArgumentException("Cannot edit flight because date cannot be created in the past");
            }
            return flightRepository.save(flight);

        }else {
            throw new IllegalArgumentException("The flight cannot updated because is not found in the database.");
        }


    }

    @Override
    public Flight getFlightById(Long id) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);
        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();


            if (flight.isStatus()){//Vuelo eliminado no podemos mostrarlo
                throw new IllegalArgumentException("The flight is not found in the database.");
            }
        }else {
            throw new IllegalArgumentException("The flight is not found in the database.");
        }

        return flightRepository.findById(id).orElse(null);
    }

}
