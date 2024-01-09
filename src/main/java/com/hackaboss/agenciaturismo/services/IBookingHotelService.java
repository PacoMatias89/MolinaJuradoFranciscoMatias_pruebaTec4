package com.hackaboss.agenciaturismo.services;

import com.hackaboss.agenciaturismo.dto.BookingHotelDto;
import com.hackaboss.agenciaturismo.dto.UpdateBookingHotelDto;
import com.hackaboss.agenciaturismo.model.BookHotel;

import java.util.List;

public interface IBookingHotelService {

    BookHotel createBookHotel(BookingHotelDto bookingHotelDto);

    List<BookHotel> getAllBookHotels();

    BookHotel getBookHotelById(Long id);

    BookHotel deleteBookHotelById(Long id);

    BookHotel editBookHotelById(Long id, UpdateBookingHotelDto bookingHotelDto);


}
