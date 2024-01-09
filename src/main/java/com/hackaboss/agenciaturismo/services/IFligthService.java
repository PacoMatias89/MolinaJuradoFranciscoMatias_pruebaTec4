package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.FlightDto;
import com.hackaboss.agenciaturismo.model.Flight;

import java.time.LocalDate;
import java.util.List;

public interface IFligthService {

    Flight createFlight(FlightDto flightDto);

    List<Flight> getAllFlightsByPlaceAndDates(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String origin, String destination);

    List<Flight> getAllFlights();

    Flight deleteFlightById(Long id);

    Flight editFlightById(Long id, FlightDto flightDto);

    Flight getFlightById(Long id);
}
