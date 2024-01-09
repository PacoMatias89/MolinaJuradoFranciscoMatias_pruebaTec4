package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.BookingFlightDto;
import com.hackaboss.agenciaturismo.dto.UpdateBookingFlightDto;
import com.hackaboss.agenciaturismo.model.BookFlight;


import java.util.List;

public interface IBookingFlightService {
    BookFlight createBookingFlight(BookingFlightDto booking);

    List<BookFlight> getAllBookFlights();

    BookFlight getFlightById(Long id);

    BookFlight deleteBookingFlightById(Long id);

    BookFlight editBookingFlightById(Long id, UpdateBookingFlightDto booking);


}
